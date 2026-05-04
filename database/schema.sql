DROP DATABASE IF EXISTS upes_lost_found;
CREATE DATABASE upes_lost_found CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE upes_lost_found;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(120) NOT NULL,
    university_id VARCHAR(40) NOT NULL,
    email VARCHAR(140) NOT NULL UNIQUE,
    phone VARCHAR(15) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_users_role(role),
    INDEX idx_users_active(active)
);

CREATE TABLE item_reports (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    report_type ENUM('LOST', 'FOUND') NOT NULL,
    item_name VARCHAR(140) NOT NULL,
    category VARCHAR(80) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(100) NOT NULL,
    item_date DATE NOT NULL,
    image_path VARCHAR(500),
    contact_name VARCHAR(120) NOT NULL,
    contact_phone VARCHAR(15) NOT NULL,
    storage_location VARCHAR(160),
    status ENUM('PENDING', 'APPROVED', 'MATCHED', 'CLAIMED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_reports_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_reports_type(report_type),
    INDEX idx_reports_category(category),
    INDEX idx_reports_location(location),
    INDEX idx_reports_status(status),
    FULLTEXT INDEX ft_reports_item_text(item_name, description)
);

CREATE TABLE claim_requests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    item_id INT NOT NULL,
    claimant_id INT NOT NULL,
    message TEXT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    admin_note VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_claim_item FOREIGN KEY (item_id) REFERENCES item_reports(id),
    CONSTRAINT fk_claim_user FOREIGN KEY (claimant_id) REFERENCES users(id),
    INDEX idx_claim_status(status)
);

CREATE TABLE notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(120) NOT NULL,
    message VARCHAR(500) NOT NULL,
    read_flag BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_notifications_user(user_id, created_at)
);
