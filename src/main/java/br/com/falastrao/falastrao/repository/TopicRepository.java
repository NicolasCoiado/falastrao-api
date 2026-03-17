package br.com.falastrao.falastrao.repository;

import br.com.falastrao.falastrao.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    Optional<Topic> findBySubject(String subject);
    Set<Topic> findBySubjectIn(Set<String> subjects);
    @Query("SELECT t FROM Topic t WHERE LOWER(t.subject) LIKE LOWER(CONCAT(:prefix, '%'))")
    List<Topic> findBySubjectStartingWith(@Param("prefix") String prefix);
}