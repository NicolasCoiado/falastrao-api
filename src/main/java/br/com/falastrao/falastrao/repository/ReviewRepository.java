package br.com.falastrao.falastrao.repository;

import br.com.falastrao.falastrao.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Review findByTitle(String title);
    Review findByTopicsSubject(String subject);
}