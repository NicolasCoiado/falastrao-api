package br.com.falastrao.falastrao.repository;

import br.com.falastrao.falastrao.model.Review;
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

    @Query("""
    SELECT
        COUNT(r),
        COUNT(DISTINCT r.user),
        MIN(r.publishedAt),
        MAX(r.publishedAt),
        SUM(CASE WHEN r.publishedAt >= :since THEN 1 ELSE 0 END)
    FROM Topic t
    JOIN t.reviews r
    WHERE t.subject = :subject
    """)
    Object[] findTopicStats(@Param("subject") String subject, @Param("since") OffsetDateTime since);

    @Query(value = """
    SELECT t2.subject, COUNT(*) as co_occurrences
    FROM review_topics rt1
    JOIN review_topics rt2 ON rt1.review_id = rt2.review_id
    JOIN topics t2 ON rt2.topic_id = t2.id
    JOIN topics t1 ON rt1.topic_id = t1.id
    WHERE t1.subject = :subject
    AND t2.subject != :subject
    GROUP BY t2.subject
    ORDER BY co_occurrences DESC
    LIMIT 5
    """, nativeQuery = true)
    List<String> findRelatedTopics(@Param("subject") String subject);

    @Query("""
    SELECT r FROM Topic t
    JOIN t.reviews r
    WHERE t.subject = :subject
    ORDER BY r.publishedAt DESC
    """)
    List<Review> findRecentReviews(@Param("subject") String subject, Pageable pageable);

}