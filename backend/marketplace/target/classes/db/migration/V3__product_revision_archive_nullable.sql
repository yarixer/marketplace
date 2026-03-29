ALTER TABLE product_revisions
    MODIFY archive_object_key VARCHAR(255) NULL,
    MODIFY archive_original_filename VARCHAR(255) NULL;