package br.com.falastrao.falastrao.repository;

import br.com.falastrao.falastrao.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Optional<Notification> findByExternalIdAndUserId(UUID externalId, Long userId);
    long countByUserIdAndReadFalse(Long userId);
}