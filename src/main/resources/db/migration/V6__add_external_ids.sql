ALTER TABLE users
    ADD COLUMN external_id UUID UNIQUE;

UPDATE users SET external_id = gen_random_uuid() WHERE external_id IS NULL;

ALTER TABLE users
    ALTER COLUMN external_id SET NOT NULL;

ALTER TABLE reviews
    ADD COLUMN external_id UUID UNIQUE;

UPDATE reviews SET external_id = gen_random_uuid() WHERE external_id IS NULL;

ALTER TABLE reviews
    ALTER COLUMN external_id SET NOT NULL;