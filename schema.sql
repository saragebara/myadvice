-- Create Courses Table
CREATE TABLE IF NOT EXISTS courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(255) NOT NULL,
    description TEXT,
    credits INT DEFAULT 3,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Prerequisites Table (many-to-many relationship)
CREATE TABLE IF NOT EXISTS prerequisites (
    prerequisite_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    prerequisite_course_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (prerequisite_course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE KEY unique_prerequisite (course_id, prerequisite_course_id)
);

-- Create Profiles Table (Extended student profiles)
CREATE TABLE IF NOT EXISTS profiles (
    profile_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL UNIQUE,
    program_id VARCHAR(50),
    enrollment_year INT,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    phone VARCHAR(20),
    address VARCHAR(255),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- Create Programs/Curricula Table
CREATE TABLE IF NOT EXISTS programs (
    program_id VARCHAR(50) PRIMARY KEY,
    program_name VARCHAR(255) NOT NULL,
    program_description TEXT,
    total_credits INT DEFAULT 120,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create Program Courses Table (Many-to-many: which courses are in which program)
CREATE TABLE IF NOT EXISTS program_courses (
    program_course_id INT AUTO_INCREMENT PRIMARY KEY,
    program_id VARCHAR(50) NOT NULL,
    course_id INT NOT NULL,
    required BOOLEAN DEFAULT TRUE,
    year_of_study INT,
    semester INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (program_id) REFERENCES programs(program_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE KEY unique_program_course (program_id, course_id)
);

-- Create Transcripts Table
CREATE TABLE IF NOT EXISTS transcripts (
    transcript_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    gpa DECIMAL(3, 2),
    total_credits_completed INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    UNIQUE KEY unique_transcript (student_id)
);

-- Create Transcript Details Table (Individual course grades)
CREATE TABLE IF NOT EXISTS transcript_details (
    transcript_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    transcript_id INT NOT NULL,
    course_id INT NOT NULL,
    grade VARCHAR(3),
    grade_points DECIMAL(3, 2),
    credits INT,
    completion_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transcript_id) REFERENCES transcripts(transcript_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

-- Sample Data
INSERT INTO programs (program_id, program_name, program_description, total_credits) VALUES
('COMP-BSC', 'Bachelor of Science in Computer Science', 'A comprehensive program in computer science with focus on software development', 120),
('COMP-ADIP', 'Advanced Diploma in Computer Systems', 'A practical program in computer systems and network management', 90);

INSERT INTO courses (course_code, course_name, description, credits) VALUES
('COMP-1000', 'Introduction to Programming', 'Learn the fundamentals of programming with Python', 3),
('COMP-1050', 'Computer Architecture', 'Introduction to computer architecture and digital logic', 3),
('COMP-2000', 'Data Structures', 'Advanced data structures and algorithms', 3),
('COMP-2800', 'Web Development', 'Build dynamic web applications', 3),
('COMP-3000', 'Database Systems', 'Design and manage database systems', 3),
('COMP-3150', 'Software Engineering', 'Software development practices and methodologies', 3);

INSERT INTO prerequisites (course_id, prerequisite_course_id) VALUES
(3, 1),
(4, 1),
(5, 3),
(6, 3);
