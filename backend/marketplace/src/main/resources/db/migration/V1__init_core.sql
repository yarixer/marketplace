CREATE TABLE users (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(120) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_roles (
    user_id BIGINT UNSIGNED NOT NULL,
    role_code VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, role_code),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE seller_profiles (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    public_name VARCHAR(120) NOT NULL,
    slug VARCHAR(160) NOT NULL,
    bio VARCHAR(2000) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_seller_profiles_user_id UNIQUE (user_id),
    CONSTRAINT uk_seller_profiles_slug UNIQUE (slug),
    CONSTRAINT fk_seller_profiles_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE wallet_accounts (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    currency CHAR(3) NOT NULL,
    available_minor BIGINT NOT NULL DEFAULT 0,
    pending_minor BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_wallet_accounts_user_id UNIQUE (user_id),
    CONSTRAINT fk_wallet_accounts_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE wallet_entries (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    wallet_account_id BIGINT UNSIGNED NOT NULL,
    entry_type VARCHAR(64) NOT NULL,
    amount_minor BIGINT NOT NULL,
    reference_type VARCHAR(64) NULL,
    reference_id BIGINT UNSIGNED NULL,
    comment VARCHAR(500) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_wallet_entries_wallet_account FOREIGN KEY (wallet_account_id) REFERENCES wallet_accounts (id),
    INDEX idx_wallet_entries_wallet_account_created_at (wallet_account_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tags (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL,
    slug VARCHAR(80) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_tags_slug UNIQUE (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE products (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    seller_id BIGINT UNSIGNED NOT NULL,
    slug VARCHAR(180) NOT NULL,
    state VARCHAR(32) NOT NULL,
    current_live_revision_id BIGINT UNSIGNED NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_products_slug UNIQUE (slug),
    CONSTRAINT fk_products_seller FOREIGN KEY (seller_id) REFERENCES seller_profiles (id),
    INDEX idx_products_seller_id (seller_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE product_revisions (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    product_id BIGINT UNSIGNED NOT NULL,
    revision_number INT NOT NULL,
    status VARCHAR(32) NOT NULL,
    title VARCHAR(160) NOT NULL,
    short_description VARCHAR(300) NOT NULL,
    description LONGTEXT NOT NULL,
    price_minor BIGINT NOT NULL,
    currency CHAR(3) NOT NULL,
    archive_object_key VARCHAR(255) NOT NULL,
    archive_original_filename VARCHAR(255) NOT NULL,
    archive_size_bytes BIGINT UNSIGNED NULL,
    checksum_sha256 VARCHAR(64) NULL,
    submitted_at DATETIME(6) NULL,
    reviewed_at DATETIME(6) NULL,
    reviewed_by_user_id BIGINT UNSIGNED NULL,
    rejection_reason VARCHAR(2000) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_product_revisions_product_rev UNIQUE (product_id, revision_number),
    CONSTRAINT fk_product_revisions_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_product_revisions_reviewed_by_user FOREIGN KEY (reviewed_by_user_id) REFERENCES users (id),
    INDEX idx_product_revisions_status_title (status, title),
    INDEX idx_product_revisions_product_status (product_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE products
    ADD CONSTRAINT fk_products_current_live_revision
    FOREIGN KEY (current_live_revision_id) REFERENCES product_revisions (id);

CREATE TABLE product_revision_images (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    revision_id BIGINT UNSIGNED NOT NULL,
    object_key VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    mime_type VARCHAR(120) NOT NULL,
    size_bytes BIGINT UNSIGNED NULL,
    sort_order INT NOT NULL DEFAULT 0,
    is_cover BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_product_revision_images_revision FOREIGN KEY (revision_id) REFERENCES product_revisions (id) ON DELETE CASCADE,
    INDEX idx_product_revision_images_revision_sort (revision_id, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE product_revision_tags (
    revision_id BIGINT UNSIGNED NOT NULL,
    tag_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (revision_id, tag_id),
    CONSTRAINT fk_product_revision_tags_revision FOREIGN KEY (revision_id) REFERENCES product_revisions (id) ON DELETE CASCADE,
    CONSTRAINT fk_product_revision_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE orders (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    buyer_id BIGINT UNSIGNED NOT NULL,
    status VARCHAR(32) NOT NULL,
    subtotal_minor BIGINT NOT NULL DEFAULT 0,
    currency CHAR(3) NOT NULL,
    payment_provider VARCHAR(40) NULL,
    payment_reference VARCHAR(255) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    paid_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_orders_buyer FOREIGN KEY (buyer_id) REFERENCES users (id),
    INDEX idx_orders_buyer_status (buyer_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_items (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    order_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    revision_id BIGINT UNSIGNED NOT NULL,
    seller_id BIGINT UNSIGNED NOT NULL,
    price_minor BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT fk_order_items_revision FOREIGN KEY (revision_id) REFERENCES product_revisions (id),
    CONSTRAINT fk_order_items_seller FOREIGN KEY (seller_id) REFERENCES seller_profiles (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE product_entitlements (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    buyer_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    granted_by_order_id BIGINT UNSIGNED NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_product_entitlements_buyer_product UNIQUE (buyer_id, product_id),
    CONSTRAINT fk_product_entitlements_buyer FOREIGN KEY (buyer_id) REFERENCES users (id),
    CONSTRAINT fk_product_entitlements_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT fk_product_entitlements_order FOREIGN KEY (granted_by_order_id) REFERENCES orders (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE product_votes (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    product_id BIGINT UNSIGNED NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    vote_value TINYINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_product_votes_product_user UNIQUE (product_id, user_id),
    CONSTRAINT fk_product_votes_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_product_votes_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT chk_product_votes_vote_value CHECK (vote_value IN (-1, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE password_reset_tokens (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    used_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_password_reset_tokens_user_id (user_id),
    INDEX idx_password_reset_tokens_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;