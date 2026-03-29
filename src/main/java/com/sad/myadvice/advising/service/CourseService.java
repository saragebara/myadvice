package com.sad.myadvice.advising.service;

import com.sad.myadvice.entity.Course;
import com.sad.myadvice.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service //tells Spring that this is a service class to manage automatically
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    //Get all courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    //Get a course by its code ("COMP2800")
    public Course getCourseByCode(String code) {
        return courseRepository.findByCode(code);
    }

    //Get all courses of a specific year level (1, 2, 3, or 4)
    public List<Course> getCoursesByYear(int yearLevel) {
        return courseRepository.findByYearLevel(yearLevel);
    }

    //Get only required courses
    public List<Course> getRequiredCourses() {
        return courseRepository.findByIsRequired(true);
    }

    //Get only elective courses (courses that are not required)
    public List<Course> getElectiveCourses() {
        return courseRepository.findByIsRequired(false);
    }

    //Get a course from a keyword, either found in its name or its course code
    public List<Course> searchCourses(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return courseRepository.findAll();
        }
        String query = keyword.trim().toLowerCase();
        return courseRepository.findAll().stream()
            .filter(c -> c.getName() != null && c.getName().toLowerCase().contains(query)
                || c.getCode() != null && c.getCode().toLowerCase().contains(query)
                || c.getDescription() != null && c.getDescription().toLowerCase().contains(query))
            .toList();
    }

    public List<Course> searchCourses(String keyword, Integer yearLevel, Boolean required, String offeredTerm) {
        String query = keyword == null ? "" : keyword.trim().toLowerCase();
        return courseRepository.findAll().stream()
            .filter(c -> query.isBlank() || (
                (c.getName() != null && c.getName().toLowerCase().contains(query))
                || (c.getCode() != null && c.getCode().toLowerCase().contains(query))
                || (c.getDescription() != null && c.getDescription().toLowerCase().contains(query))
            ))
            .filter(c -> yearLevel == null || c.getYearLevel() == yearLevel)
            .filter(c -> required == null || c.isRequired() == required)
            .filter(c -> {
                if (offeredTerm == null || offeredTerm.isBlank()) {
                    return true;
                }
                String term = offeredTerm.trim().toLowerCase();
                return switch (term) {
                    case "fall" -> c.isOfferedFall();
                    case "winter" -> c.isOfferedWinter();
                    case "summer" -> c.isOfferedSummer();
                    default -> true;
                };
            })
            .toList();
    }
}