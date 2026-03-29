SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE auth_refresh_tokens;
TRUNCATE TABLE product_revision_tags;
TRUNCATE TABLE product_revision_images;
TRUNCATE TABLE product_votes;
TRUNCATE TABLE product_entitlements;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE product_revisions;
TRUNCATE TABLE products;
TRUNCATE TABLE tags;
TRUNCATE TABLE seller_profiles;
TRUNCATE TABLE password_reset_tokens;
TRUNCATE TABLE wallet_entries;
TRUNCATE TABLE wallet_accounts;
TRUNCATE TABLE user_roles;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;