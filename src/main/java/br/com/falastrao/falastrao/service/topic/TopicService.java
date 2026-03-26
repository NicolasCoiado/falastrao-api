package br.com.falastrao.falastrao.service.topic;

import br.com.falastrao.falastrao.dto.response.PageResponse;
import br.com.falastrao.falastrao.dto.response.TopicDetailsResponse;
import br.com.falastrao.falastrao.dto.response.TopicRecentReviewResponse;
import br.com.falastrao.falastrao.dto.response.TrendingTopicResponse;
import br.com.falastrao.falastrao.exception.InvalidRequestException;
import br.com.falastrao.falastrao.exception.TopicAlreadyExistsException;
import br.com.falastrao.falastrao.exception.TopicNotFoundException;
import br.com.falastrao.falastrao.model.Topic;
import br.com.falastrao.falastrao.repository.TopicRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.text.Normalizer;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TopicService {

    private final TopicRepository repository;

    public TopicService(TopicRepository repository) {
        this.repository = repository;
    }

    public PageResponse<String> getTopics (int page, int size){
        PageRequest pageable = PageRequest.of(page, size, Sort.by("subject").ascending());
        Page<String> result = repository.findAll(pageable)
                .map(Topic::getSubject);
        return PageResponse.from(result);
    }

    @Transactional
    public Set<Topic> verifyAndCreateTopics (Set<String> topicsRequest) {

        if (topicsRequest == null || topicsRequest.isEmpty()) {
            return new HashSet<>();
        }

        Set<String> normalizedSubjects = topicsRequest.stream()
                .map(this::normalize)
                .collect(Collectors.toSet());

        Set<Topic> existingTopics = repository.findBySubjectIn(normalizedSubjects);

        Set<String> existingSubjects = existingTopics.stream()
                .map(Topic::getSubject)
                .collect(Collectors.toSet());

        Set<String> subjectsToCreate = normalizedSubjects.stream()
                .filter(subject -> !existingSubjects.contains(subject))
                .collect(Collectors.toSet());

        Set<Topic> savedTopics = new HashSet<>(existingTopics);

        for (String subject : subjectsToCreate) {
            Topic topic = new Topic();
            topic.setSubject(subject);

            try {
                savedTopics.add(repository.save(topic));
            } catch (DataIntegrityViolationException ex) {
                repository.findBySubject(subject)
                        .ifPresent(savedTopics::add);
            }
        }

        return savedTopics;
    }

    private String normalize(String subject) {

        String normalized = Normalizer.normalize(subject, Normalizer.Form.NFD);

        return normalized
                .replaceAll("\\p{M}", "")
                .trim()
                .toLowerCase();
    }

    public List<String> searchTopics(String prefix) {
        if (prefix == null || prefix.isBlank()) return List.of();
        return repository.findBySubjectStartingWith(prefix)
                .stream()
                .map(Topic::getSubject)
                .toList();
    }

    @Cacheable(value = "rankedTopics", key = "#limit")
    public List<String> getRankedTopics(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return repository.findTopicsByUsage(pageable)
                .stream()
                .map(Topic::getSubject)
                .toList();
    }

    @CacheEvict(value = "topicDetails", allEntries = true)
    public String updateTopic(String oldSubject, String newSubject) {
        if (oldSubject == null || newSubject == null || oldSubject.isBlank() || newSubject.isBlank()) {
            throw new InvalidRequestException("Subjects cannot be null or blank");
        }

        String normalizedOldSubject = normalize(oldSubject);
        String normalizedNewSubject = normalize(newSubject);

        Topic existingTopic = repository.findBySubject(normalizedOldSubject)
                .orElseThrow(() -> new TopicNotFoundException("Topic not found: " + oldSubject));

        if (repository.existsBySubject(normalizedNewSubject)) {
            throw new TopicAlreadyExistsException("Topic already exists: " + newSubject);
        }

        existingTopic.setSubject(normalizedNewSubject);

        return repository.save(existingTopic).getSubject();
    }

    public List<String> getUnusedTopics() {
        return repository.findUnusedTopics()
                .stream()
                .map(Topic::getSubject)
                .toList();
    }

    public List<TrendingTopicResponse> getTrendingTopics(OffsetDateTime since) {
        Pageable pageable = PageRequest.of(0, 10);

        return repository.findTrendingTopics(since, pageable)
                .stream()
                .map(row -> new TrendingTopicResponse(
                        (String) row[0],
                        (Long) row[1]
                ))
                .toList();
    }

    @Cacheable(value = "topicDetails", key = "#subject")
    public TopicDetailsResponse getTopicDetails(String subject) {
        String normalizedSubject = normalize(subject);

        if (!repository.existsBySubject(normalizedSubject)) {
            throw new TopicNotFoundException("Topic not found: " + subject);
        }

        OffsetDateTime oneMonthAgo = OffsetDateTime.now().minusMonths(1);
        Pageable fiveReviews = PageRequest.of(0, 5);

        Object[] rawStats = repository.findTopicStats(normalizedSubject, oneMonthAgo);

        Object[] stats = (rawStats.length > 0 && rawStats[0] instanceof Object[])
                ? (Object[]) rawStats[0]
                : rawStats;

        long totalReviews    = stats[0] != null ? ((Number) stats[0]).longValue() : 0L;
        long distinctAuthors = stats[1] != null ? ((Number) stats[1]).longValue() : 0L;
        long reviewsLastMonth = stats[4] != null ? ((Number) stats[4]).longValue() : 0L;

        OffsetDateTime firstUsed = toOffsetDateTime(stats[2]);
        OffsetDateTime lastUsed  = toOffsetDateTime(stats[3]);

        List<String> relatedTopics = repository.findRelatedTopics(normalizedSubject);

        List<TopicRecentReviewResponse> recentReviews = repository
                .findRecentReviews(normalizedSubject, fiveReviews)
                .stream()
                .map(r -> new TopicRecentReviewResponse(
                        r.getExternalId(),
                        r.getTitle(),
                        r.getUser().getUsername(),
                        r.getPublishedAt().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                ))
                .toList();

        return new TopicDetailsResponse(
                normalizedSubject,
                totalReviews,
                distinctAuthors,
                firstUsed != null ? firstUsed.format(DateTimeFormatter.ISO_LOCAL_DATE) : null,
                lastUsed  != null ? lastUsed.format(DateTimeFormatter.ISO_LOCAL_DATE)  : null,
                reviewsLastMonth,
                relatedTopics,
                recentReviews
        );
    }

    private OffsetDateTime toOffsetDateTime(Object value) {
        if (value == null) return null;
        if (value instanceof OffsetDateTime odt) return odt;
        if (value instanceof java.sql.Timestamp ts) {
            return ts.toInstant().atOffset(java.time.ZoneOffset.UTC);
        }
        if (value instanceof java.time.LocalDateTime ldt) {
            return ldt.atOffset(java.time.ZoneOffset.UTC);
        }
        throw new IllegalArgumentException("Cannot convert to OffsetDateTime: " + value.getClass());
    }

    @Transactional
    @CacheEvict(value = "topicDetails", allEntries = true)
    public void deleteTopic(String subject) {
        String normalizedSubject = normalize(subject);

        Topic topic = repository.findBySubject(normalizedSubject)
                .orElseThrow(() -> new TopicNotFoundException("Topic not found: " + subject));

        repository.delete(topic);
    }

    public int deleteUnusedTopics() {
        List<Topic> unusedTopics = repository.findUnusedTopics();
        int count = unusedTopics.size();
        repository.deleteAll(unusedTopics);
        return count;
    }

    public void deleteTopicById(Long id) {
        if (!repository.existsById(id)) {
            throw new TopicNotFoundException("Topic not found with id: " + id);
        }
        repository.deleteById(id);
    }

}