package com.sad.myadvice.repository;

import com.sad.myadvice.entity.Prerequisite;
import com.sad.myadvice.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrerequisiteRepository extends JpaRepository<Prerequisite, Long> {
    List<Prerequisite> findByCourse(Course course);
    List<Prerequisite> findByCourseAndType(Course course, Prerequisite.Type type);
    List<Prerequisite> findByRequiredCourse(Course requiredCourse);
}