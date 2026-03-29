package com.sad.myadvice;

import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.Transcript;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.CourseRepository;
import com.sad.myadvice.repository.TranscriptRepository;
import com.sad.myadvice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(4) //runs after degreerequirements seeder
public class TranscriptDataSeeder implements CommandLineRunner {
    //repository
    private final TranscriptRepository transcriptRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    public TranscriptDataSeeder(TranscriptRepository transcriptRepository, UserRepository userRepository,
                                CourseRepository courseRepository) {
        this.transcriptRepository = transcriptRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) {
        if (transcriptRepository.count() > 0) return; //if there's already transcript info then skip

        //get test student from DataSeeder
        User student = userRepository.findByStudentId("110163845");
        if (student == null) { //if not found
            System.out.println("⚠ TranscriptDataSeeder: test student not found, skipping.");
            return;
        }

        //Completed courses — Year 1 core
        addTranscript(student, "COMP-1000", Transcript.Status.COMPLETED, "2024F", 85.0);
        addTranscript(student, "COMP-1400", Transcript.Status.COMPLETED, "2024F", 78.0);
        addTranscript(student, "COMP-1410", Transcript.Status.COMPLETED, "2025W", 82.0);
        addTranscript(student, "MATH-1250", Transcript.Status.COMPLETED, "2024F", 74.0);
        addTranscript(student, "MATH-1720", Transcript.Status.COMPLETED, "2024F", 69.0);

        //Completed courses — Year 2 core
        addTranscript(student, "COMP-2120", Transcript.Status.COMPLETED, "2025F", 88.0);
        addTranscript(student, "COMP-2540", Transcript.Status.COMPLETED, "2025F", 76.0);
        addTranscript(student, "COMP-2560", Transcript.Status.COMPLETED, "2025F", 80.0);
        addTranscript(student, "COMP-2650", Transcript.Status.COMPLETED, "2025W", 72.0);
        addTranscript(student, "STAT-2910", Transcript.Status.COMPLETED, "2025W", 83.0);

        //CSSE specific completed
        addTranscript(student, "COMP-2140", Transcript.Status.COMPLETED, "2025W", 77.0);
        addTranscript(student, "COMP-2310", Transcript.Status.COMPLETED, "2025W", 71.0);
        addTranscript(student, "MATH-1020", Transcript.Status.COMPLETED, "2025W", 68.0);
        addTranscript(student, "MATH-1730", Transcript.Status.COMPLETED, "2025W", 73.0);

        //Currently in progress — Year 3
        addTranscript(student, "COMP-2800", Transcript.Status.IN_PROGRESS, "2026W", null);
        addTranscript(student, "COMP-3150", Transcript.Status.IN_PROGRESS, "2026W", null);
        addTranscript(student, "COMP-3220", Transcript.Status.IN_PROGRESS, "2026W", null);

        System.out.println("✓ Transcript data seeded for " + student.getName());
    }

    private void addTranscript(User student, String courseCode, Transcript.Status status, String term, Double grade) {
        Course course = courseRepository.findByCode(courseCode);
        if (course == null) {
            System.out.println("⚠ Course not found: " + courseCode);
            return;
        }
        Transcript t = new Transcript();
        t.setStudent(student);
        t.setCourse(course);
        t.setStatus(status);
        t.setTerm(term);
        t.setGrade(grade);
        transcriptRepository.save(t);
    }
}