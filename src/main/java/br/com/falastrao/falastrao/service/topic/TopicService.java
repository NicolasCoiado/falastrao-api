package br.com.falastrao.falastrao.service.topic;

import br.com.falastrao.falastrao.dto.response.PageResponse;
import br.com.falastrao.falastrao.model.Topic;
import br.com.falastrao.falastrao.repository.TopicRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.text.Normalizer;
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

}