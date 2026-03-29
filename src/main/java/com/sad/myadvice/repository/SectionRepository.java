package com.sad.myadvice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sad.myadvice.entity.AdminCourse;
import com.sad.myadvice.entity.Instructor;
import com.sad.myadvice.entity.Room;
import com.sad.myadvice.entity.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByCourse(AdminCourse course);
    List<Section> findByInstructor(Instructor instructor);
    List<Section> findByRoom(Room room);
}

