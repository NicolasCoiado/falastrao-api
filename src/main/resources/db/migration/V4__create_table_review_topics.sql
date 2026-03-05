CREATE TABLE review_topics (
    review_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,

    CONSTRAINT pk_review_topics
        PRIMARY KEY (review_id, topic_id),

    CONSTRAINT fk_review_topics_review
        FOREIGN KEY (review_id)
        REFERENCES reviews (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_review_topics_topic
        FOREIGN KEY (topic_id)
        REFERENCES topics (id)
        ON DELETE CASCADE
);