package br.com.falastrao.falastrao.repository;

import br.com.falastrao.falastrao.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    Topic findBySubject(String subject);
}