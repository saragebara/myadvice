package com.sad.myadvice.repository;

import com.sad.myadvice.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByYearLevel(int yearLevel);
    List<Course> findByIsRequired(boolean isRequired);
    Course findByCode(String code);
}