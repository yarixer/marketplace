INSERT INTO users (id, email, password_hash, display_name, enabled)
VALUES
    (1, 'admin@test.local', 'x', 'Admin', TRUE),
    (2, 'seller@test.local', 'x', 'VoxelMaster', TRUE),
    (3, 'buyer1@test.local', 'x', 'Buyer One', TRUE),
    (4, 'buyer2@test.local', 'x', 'Buyer Two', TRUE);

INSERT INTO user_roles (user_id, role_code)
VALUES
    (1, 'ADMIN'),
    (2, 'SELLER'),
    (2, 'BUYER'),
    (3, 'BUYER'),
    (4, 'BUYER');

INSERT INTO seller_profiles (id, user_id, public_name, slug, bio)
VALUES
    (10, 2, 'VoxelMaster', 'voxelmaster', 'Test seller profile');

INSERT INTO tags (id, name, slug)
VALUES
    (100, 'Easter', 'easter'),
    (101, 'Seasonal', 'seasonal'),
    (102, 'Voxel', 'voxel');

INSERT INTO products (id, seller_id, slug, state, current_live_revision_id)
VALUES
    (1000, 10, 'easter-eggs-2026', 'ACTIVE', NULL),
    (1001, 10, 'winter-crystals-bundle', 'ACTIVE', NULL),
    (1002, 10, 'hidden-draft-pack', 'ACTIVE', NULL),
    (1003, 10, 'archived-pack', 'ARCHIVED', NULL);

INSERT INTO product_revisions (
    id,
    product_id,
    revision_number,
    status,
    title,
    short_description,
    description,
    price_minor,
    currency,
    archive_object_key,
    archive_original_filename,
    archive_size_bytes,
    checksum_sha256,
    submitted_at,
    reviewed_at,
    reviewed_by_user_id,
    rejection_reason
)
VALUES
    (
        2000,
        1000,
        1,
        'APPROVED',
        'Easter Eggs 2026',
        'Seasonal voxel egg pack.',
        'Approved easter pack for testing.',
        499,
        'USD',
        'test/easter-eggs-2026.zip',
        'easter-eggs-2026.zip',
        1024,
        'sha-easter',
        NOW(6),
        NOW(6),
        1,
        NULL
    ),
    (
        2001,
        1001,
        1,
        'APPROVED',
        'Winter Crystals Bundle',
        'Frozen crystal pack.',
        'Approved winter pack for testing.',
        699,
        'USD',
        'test/winter-crystals-bundle.zip',
        'winter-crystals-bundle.zip',
        2048,
        'sha-winter',
        NOW(6),
        NOW(6),
        1,
        NULL
    ),
    (
        2002,
        1002,
        1,
        'PENDING_REVIEW',
        'Hidden Draft Pack',
        'Should not be visible.',
        'Pending product revision.',
        999,
        'USD',
        NULL,
        NULL,
        4096,
        'sha-hidden',
        NOW(6),
        NULL,
        NULL,
        NULL
    ),
    (
        2003,
        1003,
        1,
        'APPROVED',
        'Archived Pack',
        'Archived and hidden.',
        'Archived product revision.',
        299,
        'USD',
        'test/archived-pack.zip',
        'archived-pack.zip',
        512,
        'sha-archived',
        NOW(6),
        NOW(6),
        1,
        NULL
    );

UPDATE products SET current_live_revision_id = 2000 WHERE id = 1000;
UPDATE products SET current_live_revision_id = 2001 WHERE id = 1001;
UPDATE products SET current_live_revision_id = 2002 WHERE id = 1002;
UPDATE products SET current_live_revision_id = 2003 WHERE id = 1003;

INSERT INTO product_revision_tags (revision_id, tag_id)
VALUES
    (2000, 100),
    (2000, 101),
    (2000, 102),
    (2001, 101),
    (2001, 102),
    (2002, 100),
    (2003, 100);

INSERT INTO product_votes (id, product_id, user_id, vote_value)
VALUES
    (3000, 1000, 1, 1),
    (3001, 1000, 3, 1),
    (3002, 1000, 4, -1);