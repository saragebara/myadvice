package com.sad.myadvice.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.sad.myadvice.entity.AdminCourse;

public interface AdminCourseRepository extends JpaRepository<AdminCourse, Long> {
}
