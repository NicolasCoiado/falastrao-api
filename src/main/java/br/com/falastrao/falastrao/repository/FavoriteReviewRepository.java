package br.com.falastrao.falastrao.repository;

import br.com.falastrao.falastrao.model.FavoriteReview;
import br.com.falastrao.falastrao.model.Review;
import br.com.falastrao.falastrao.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteReviewRepository extends JpaRepository<FavoriteReview, Long> {

    boolean existsByUserAndReview(User user, Review review);

    void deleteByUserAndReview(User user, Review review);

    @Query("""
            SELECT fr.review FROM FavoriteReview fr
            WHERE fr.user = :user
              AND (fr.review.privateReview = false OR fr.review.user = :user)
            ORDER BY fr.favoritedAt DESC
            """)
    Page<Review> findFavoriteReviewsByUser(@Param("user") User user, Pageable pageable);
}
