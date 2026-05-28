CREATE DATABASE IF NOT EXISTS campushub CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE campushub;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_no VARCHAR(32) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'REQUESTER',
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    admin BOOLEAN NOT NULL DEFAULT FALSE,
    nickname VARCHAR(30) NOT NULL,
    college VARCHAR(80) NULL,
    contact VARCHAR(120) NULL,
    credit_score INT NOT NULL DEFAULT 100,
    auth_token VARCHAR(512) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_student_no UNIQUE (student_no),
    CONSTRAINT ck_users_role CHECK (role IN ('REQUESTER', 'PROVIDER', 'ADMIN')),
    CONSTRAINT ck_users_status CHECK (status IN ('ACTIVE', 'DISABLED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE refresh_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(128) NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_refresh_tokens_token UNIQUE (token),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE help_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    publisher_id BIGINT NOT NULL,
    category VARCHAR(32) NOT NULL,
    title VARCHAR(80) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(100) NOT NULL,
    expected_time DATETIME NOT NULL,
    reward DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
    audit_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_help_requests_publisher FOREIGN KEY (publisher_id) REFERENCES users(id),
    CONSTRAINT ck_help_requests_category CHECK (category IN ('EXPRESS_PICKUP', 'STUDY_TUTORING', 'SECOND_HAND', 'TEAM_UP', 'OTHER')),
    CONSTRAINT ck_help_requests_status CHECK (status IN ('OPEN', 'LOCKED', 'DONE', 'CANCELLED')),
    CONSTRAINT ck_help_requests_audit CHECK (audit_status IN ('PENDING', 'APPROVED', 'REJECTED')),
    CONSTRAINT ck_help_requests_reward CHECK (reward >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACCEPTED',
    accepted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_at DATETIME NULL,
    started_at DATETIME NULL,
    completed_at DATETIME NULL,
    reviewed_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_orders_request_id UNIQUE (request_id),
    CONSTRAINT fk_orders_request FOREIGN KEY (request_id) REFERENCES help_requests(id),
    CONSTRAINT fk_orders_requester FOREIGN KEY (requester_id) REFERENCES users(id),
    CONSTRAINT fk_orders_provider FOREIGN KEY (provider_id) REFERENCES users(id),
    CONSTRAINT ck_orders_status CHECK (status IN ('ACCEPTED', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'REVIEWED', 'CANCELLED')),
    CONSTRAINT ck_orders_different_users CHECK (requester_id <> provider_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    reviewee_id BIGINT NOT NULL,
    rating TINYINT NOT NULL,
    comment VARCHAR(500) NULL,
    credit_delta INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_reviews_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id),
    CONSTRAINT fk_reviews_reviewee FOREIGN KEY (reviewee_id) REFERENCES users(id),
    CONSTRAINT uk_reviews_order_reviewer UNIQUE (order_id, reviewer_id),
    CONSTRAINT ck_reviews_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT ck_reviews_different_users CHECK (reviewer_id <> reviewee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    receiver_id BIGINT NOT NULL,
    type VARCHAR(40) NOT NULL,
    title VARCHAR(80) NOT NULL,
    content VARCHAR(500) NOT NULL,
    read_flag BOOLEAN NOT NULL DEFAULT FALSE,
    related_type VARCHAR(40) NULL,
    related_id BIGINT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at DATETIME NULL,
    CONSTRAINT fk_notifications_receiver FOREIGN KEY (receiver_id) REFERENCES users(id),
    CONSTRAINT ck_notifications_type CHECK (type IN ('ORDER_ACCEPTED', 'ORDER_CONFIRMED', 'ORDER_STARTED', 'ORDER_COMPLETED', 'REVIEW_CREATED', 'AUDIT_RESULT'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    target_type VARCHAR(40) NOT NULL,
    target_id BIGINT NOT NULL,
    action VARCHAR(40) NOT NULL,
    reason VARCHAR(500) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_logs_admin FOREIGN KEY (admin_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE INDEX idx_help_requests_category ON help_requests(category);
CREATE INDEX idx_help_requests_status ON help_requests(status);
CREATE INDEX idx_help_requests_audit_status ON help_requests(audit_status);
CREATE INDEX idx_help_requests_expected_time ON help_requests(expected_time);
CREATE INDEX idx_help_requests_created_at ON help_requests(created_at);
CREATE INDEX idx_help_requests_browse ON help_requests(audit_status, status, category, created_at);
CREATE INDEX idx_help_requests_publisher ON help_requests(publisher_id);
CREATE INDEX idx_orders_requester ON orders(requester_id);
CREATE INDEX idx_orders_provider ON orders(provider_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_reviews_reviewee ON reviews(reviewee_id);
CREATE INDEX idx_notifications_receiver_read_created ON notifications(receiver_id, read_flag, created_at);
CREATE INDEX idx_audit_logs_target ON audit_logs(target_type, target_id);
CREATE INDEX idx_audit_logs_admin ON audit_logs(admin_id);
