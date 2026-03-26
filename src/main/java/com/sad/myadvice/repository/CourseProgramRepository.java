package com.sad.myadvice.repository;

import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.CourseProgram;
import com.sad.myadvice.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseProgramRepository extends JpaRepository<CourseProgram, Long> {
    //finding by course
    List<CourseProgram> findByCourse(Course course);
    //finding based on major/program
    List<CourseProgram> findByMajor(Major major);
    boolean existsByCourseAndMajor(Course course, Major major);
}