package com.sad.myadvice;

import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.entity.Major;
import com.sad.myadvice.repository.CourseRepository;
import com.sad.myadvice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public DataSeeder(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) {

        //only seeds if the user DB is empty
        if (userRepository.count() > 0) return;

        //test student
        User student = new User();
        student.setName("Studious Student");
        student.setEmail("student@uwindsor.ca");
        student.setRole(User.Role.STUDENT);
        student.setStudentId("110163845");
        student.setPassword("password");
        student.setMajor(Major.CSH);
        userRepository.save(student);

        //test faculty
        User faculty = new User();
        faculty.setName("Dr. Faculty");
        faculty.setEmail("faculty@uwindsor.ca");
        faculty.setRole(User.Role.FACULTY);
        faculty.setStudentId(null);
        faculty.setPassword("password");
        userRepository.save(faculty);

        //test staff
        User staff = new User();
        staff.setName("CS Staff");
        staff.setEmail("staff@uwindsor.ca");
        staff.setRole(User.Role.STAFF);
        staff.setStudentId(null);
        staff.setPassword("password");
        userRepository.save(staff);

        System.out.println("✓ Test data seeded successfully");
    }
}