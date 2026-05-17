package br.com.falastrao.falastrao.repository;

import br.com.falastrao.falastrao.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByExternalId(UUID externalId);

    @Query("SELECT u FROM User u WHERE u.lastLogin < :threshold OR (u.lastLogin IS NULL AND u.createdAt < :threshold)")
    Page<User> findInactiveUsers(@Param("threshold") OffsetDateTime threshold, Pageable pageable);
}