package com.sad.myadvice.adminRepo;


import org.springframework.data.jpa.repository.JpaRepository;

import com.sad.myadvice.adminEntity.AdminCourse;

public interface AdminCourseRepository extends JpaRepository<AdminCourse, Long> {
}
