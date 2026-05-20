package br.com.falastrao.falastrao.repository;

import br.com.falastrao.falastrao.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByPrivateReviewFalse(Pageable pageable);
    Optional<Review> findByExternalIdAndPrivateReviewFalse(UUID externalId);
    Optional<Review> findByExternalId(UUID externalId);

    @Query(value = """
            SELECT DISTINCT r FROM Review r
            JOIN r.topics t
            WHERE r.privateReview = false
              AND t.subject IN :topics
            """,
            countQuery = """
            SELECT COUNT(DISTINCT r) FROM Review r
            JOIN r.topics t
            WHERE r.privateReview = false
              AND t.subject IN :topics
            """)
    Page<Review> findAllByTopicsIn(@Param("topics") Set<String> topics, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.user.externalId = :userExternalId AND (:includePrivate = true OR r.privateReview = false)")
    Page<Review> findByUserExternalId(
            @Param("userExternalId") UUID userExternalId,
            @Param("includePrivate") boolean includePrivate,
            Pageable pageable
    );
}