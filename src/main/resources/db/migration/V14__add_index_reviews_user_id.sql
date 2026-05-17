CREATE INDEX idx_reviews_user_published ON reviews (user_id, published_at DESC);
CREATE INDEX idx_reviews_user_private_published ON reviews (user_id, private_review, published_at DESC);
