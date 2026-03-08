package com.sad.myadvice.repository;

import com.sad.myadvice.entity.Transcript;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TranscriptRepository extends JpaRepository<Transcript, Long> {
    List<Transcript> findByStudent(User student);
    List<Transcript> findByStudentAndStatus(User student, Transcript.Status status);
    Transcript findByStudentAndCourse(User student, Course course);
}