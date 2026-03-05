CREATE TABLE reviews (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    thumbnail_url VARCHAR(255),
    content TEXT NOT NULL,
    published_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_reviews_users FOREIGN KEY (user_id) REFERENCES users(id)
);