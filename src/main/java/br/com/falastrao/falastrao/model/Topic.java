package br.com.falastrao.falastrao.models;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "topics")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String subject;

    @ManyToMany(mappedBy = "topics")
    private Set<Review> reviews;
}
