INSERT INTO contact (email, phone_number, link_precedence, created_at, updated_at)
VALUES ('test1@example.com', '1234567890', 'PRIMARY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO contact (email, phone_number, link_precedence, created_at, updated_at, linked_id)
VALUES ('test2@example.com', '1234567891', 'SECONDARY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
