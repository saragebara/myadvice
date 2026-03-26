package com.sad.myadvice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sad.myadvice.adminEntity.AdminCourse;
import com.sad.myadvice.adminRepo.AdminCourseRepository;

@SpringBootTest
public class RepositoryTests {

    @Autowired
    private AdminCourseRepository courseRepository;

    @Test
    public void testFindAllCourses() {
        List<AdminCourse> courses = courseRepository.findAll();
        assertNotNull(courses, "Repository returned null");
        System.out.println("Courses in DB: " + courses.size());
    }
}