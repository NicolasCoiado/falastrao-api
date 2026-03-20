package br.com.falastrao.falastrao.repository;

import br.com.falastrao.falastrao.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTitle(String title);
    List<Review> findByTopicsSubject(String subject);
    Optional<Review> findByExternalId(UUID externalId);
}