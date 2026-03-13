package br.com.falastrao.falastrao.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "topics")
@Data
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String subject;

    @ManyToMany(mappedBy = "topics")
    private Set<Review> reviews;
}
