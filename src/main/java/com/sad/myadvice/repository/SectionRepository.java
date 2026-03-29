package com.sad.myadvice.repository;

import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.Instructor;
import com.sad.myadvice.entity.Room;
import com.sad.myadvice.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// FIX: was findByCourse(AdminCourse) — now uses shared Course entity
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByCourse(Course course);
    List<Section> findByInstructor(Instructor instructor);
    List<Section> findByRoom(Room room);
}