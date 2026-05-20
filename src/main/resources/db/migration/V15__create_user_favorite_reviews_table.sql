CREATE TABLE user_favorite_reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    review_id BIGINT NOT NULL,
    favorited_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_favorites_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_favorites_review
        FOREIGN KEY (review_id)
            REFERENCES reviews(id)
            ON DELETE CASCADE,

    CONSTRAINT uq_user_favorite_review
        UNIQUE (user_id, review_id)
);

CREATE INDEX idx_user_favorite_reviews_user_id ON user_favorite_reviews(user_id);
