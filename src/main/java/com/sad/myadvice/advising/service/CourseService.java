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
        //findAll() gets all the courses from the DB as a list
        //stream() converts the list into a stream that can be filtered
        //filter(c->) keeps only the courses c where the condition is true, either the name or the code contains keyword
        return courseRepository.findAll().stream().filter(c -> c.getName().toLowerCase().contains(keyword.toLowerCase())
            || c.getCode().toLowerCase().contains(keyword.toLowerCase()))
            .toList(); //toList() converts it back to a list
    }
}
