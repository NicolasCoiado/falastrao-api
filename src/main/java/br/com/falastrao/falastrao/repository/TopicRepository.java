package br.com.falastrao.falastrao.repository;

import br.com.falastrao.falastrao.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    Optional<Topic> findBySubject(String subject);
    Set<Topic> findBySubjectIn(Set<String> subjects);

    @Query("SELECT t FROM Topic t WHERE LOWER(t.subject) LIKE LOWER(CONCAT(:prefix, '%'))")
    List<Topic> findBySubjectStartingWith(@Param("prefix") String prefix);

    @Query("SELECT t FROM Topic t LEFT JOIN t.reviews r GROUP BY t ORDER BY COUNT(r) DESC")
    List<Topic> findTopicsByUsage(Pageable pageable);

    boolean existsBySubject(String subject);

    @Query("SELECT t FROM Topic t WHERE t.reviews IS EMPTY")
    List<Topic> findUnusedTopics();

    @Query("""
    SELECT t.subject, COUNT(r) as usageCount
    FROM Topic t
    JOIN t.reviews r
    WHERE r.publishedAt >= :since
    GROUP BY t.subject
    ORDER BY usageCount DESC
    """)
    List<Object[]> findTrendingTopics(@Param("since") OffsetDateTime since, Pageable pageable);

}