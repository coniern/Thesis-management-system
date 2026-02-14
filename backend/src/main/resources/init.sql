-- 创建数据库
CREATE DATABASE IF NOT EXISTS thesis_management;
USE thesis_management;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,
    department VARCHAR(100),
    major VARCHAR(100),
    contact VARCHAR(100),
    max_students INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 角色权限表
CREATE TABLE IF NOT EXISTS roles_permissions (
    role_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) UNIQUE NOT NULL,
    permissions JSON,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 系统设置表
CREATE TABLE IF NOT EXISTS system_settings (
    setting_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    stage_name VARCHAR(100) UNIQUE NOT NULL,
    start_time DATETIME,
    end_time DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 师生互选表
CREATE TABLE IF NOT EXISTS teacher_student_selection (
    selection_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    apply_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    review_time DATETIME,
    bind_time DATETIME,
    unbind_time DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 选题表
CREATE TABLE IF NOT EXISTS topic_selections (
    topic_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    topic_name VARCHAR(255) NOT NULL,
    topic_description TEXT,
    submit_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    review_status VARCHAR(20),
    review_opinion TEXT,
    review_time DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 任务书表
CREATE TABLE IF NOT EXISTS assignment_tasks (
    task_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    topic_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    issue_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deadline DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (topic_id) REFERENCES topic_selections(topic_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 开题报告表
CREATE TABLE IF NOT EXISTS proposal_reports (
    report_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    topic_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    file_path VARCHAR(255),
    submit_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    review_status VARCHAR(20),
    review_opinion TEXT,
    review_time DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (topic_id) REFERENCES topic_selections(topic_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 中期检查表
CREATE TABLE IF NOT EXISTS midterm_reports (
    report_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    topic_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    file_path VARCHAR(255),
    submit_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    review_status VARCHAR(20),
    review_opinion TEXT,
    review_time DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (topic_id) REFERENCES topic_selections(topic_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 论文表
CREATE TABLE IF NOT EXISTS papers (
    paper_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    topic_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    file_path VARCHAR(255),
    submit_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    review_status VARCHAR(20),
    review_opinion TEXT,
    review_time DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (topic_id) REFERENCES topic_selections(topic_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 文件表
CREATE TABLE IF NOT EXISTS files (
    file_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    related_id BIGINT,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    uploader_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    file_type VARCHAR(50),
    version INT DEFAULT 1,
    sync_status VARCHAR(20) DEFAULT 'pending',
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (uploader_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 进度表
CREATE TABLE IF NOT EXISTS progress (
    progress_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT UNIQUE NOT NULL,
    teacher_id BIGINT NOT NULL,
    topic_status VARCHAR(20),
    task_status VARCHAR(20),
    proposal_status VARCHAR(20),
    midterm_status VARCHAR(20),
    paper_status VARCHAR(20),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 通知表
CREATE TABLE IF NOT EXISTS notifications (
    notification_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    publisher_id BIGINT,
    receive_role VARCHAR(20),
    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (publisher_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- 创建索引
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_department ON users(department);
CREATE INDEX idx_users_major ON users(major);
CREATE INDEX idx_selection_student ON teacher_student_selection(student_id);
CREATE INDEX idx_selection_teacher ON teacher_student_selection(teacher_id);
CREATE INDEX idx_selection_status ON teacher_student_selection(status);
CREATE INDEX idx_topic_student ON topic_selections(student_id);
CREATE INDEX idx_topic_teacher ON topic_selections(teacher_id);
CREATE INDEX idx_topic_status ON topic_selections(review_status);
CREATE INDEX idx_files_uploader ON files(uploader_id);
CREATE INDEX idx_files_receiver ON files(receiver_id);
CREATE INDEX idx_files_sync ON files(sync_status);
CREATE INDEX idx_progress_student ON progress(student_id);
CREATE INDEX idx_progress_teacher ON progress(teacher_id);

-- 插入管理员账号（密码：admin123）
INSERT INTO users (username, password, name, role) VALUES 
('admin', '$2a$10$eF693pDd0z1L7uX5L6Q2CeuW4x3y5z6A7b8c9d0e1f2g3h4i5j6k7l8m9n0o', '管理员', 'admin');

-- 插入角色权限
INSERT INTO roles_permissions (role_name, permissions) VALUES 
('admin', '{"permissions": ["user:manage", "system:config", "data:backup", "stats:view"]}'),
('teacher', '{"permissions": ["student:manage", "topic:review", "file:upload", "progress:view"]}'),
('student', '{"permissions": ["topic:submit", "file:upload", "progress:view"]}');

-- 插入系统默认阶段设置
INSERT INTO system_settings (stage_name) VALUES 
('选题'),
('开题'),
('中期检查'),
('论文提交');
