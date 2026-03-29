package com.sad.myadvice.adminServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sad.myadvice.adminEntity.AdminCourse;
import com.sad.myadvice.adminRepo.AdminCourseRepository;

@Service
public class CourseService {
    private final AdminCourseRepository courseRepository;

    // Constructor Injection
    public CourseService(AdminCourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // Get all courses, and id
    public List<AdminCourse> getAllCourses() {
        return courseRepository.findAll();
    }

    public AdminCourse getCourseById(Long id) {
        return courseRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    // create / update / delete course
    public AdminCourse createCourse(AdminCourse course) {
        return courseRepository.save(course);
    }

    public AdminCourse updateCourse(Long id, AdminCourse updatedCourse) {
        AdminCourse existing = getCourseById(id);
        existing.setCourseCode(updatedCourse.getCourseCode());
        existing.setCourseName(updatedCourse.getCourseName());
        return courseRepository.save(existing);
    }

    // delete course by id
    public void deleteCourse(Long id) {
        AdminCourse course = getCourseById(id);
        courseRepository.delete(course);
    }

    // add prerequisite to course
    public AdminCourse addPrerequisite(Long courseId, Long prereqId) {
        AdminCourse course = getCourseById(courseId);
        AdminCourse prereq = getCourseById(prereqId);
        // Cannot be its own prerequisite
        if (courseId.equals(prereqId)) {
            throw new RuntimeException( "Course cannot be its own prerequisite");
        }
        course.getPrerequisites().add(prereq);
        return courseRepository.save(course);
    }

    // remove prerequisite from course
    public AdminCourse removePrerequisite(Long courseId, Long prereqId) {
        AdminCourse course = getCourseById(courseId);
        AdminCourse prereq = getCourseById(prereqId);
        course.getPrerequisites().remove(prereq);
        return courseRepository.save(course);
    }
}