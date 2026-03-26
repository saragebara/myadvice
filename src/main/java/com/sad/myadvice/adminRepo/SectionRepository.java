package com.sad.myadvice.adminRepo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sad.myadvice.adminEntity.AdminCourse;
import com.sad.myadvice.adminEntity.Instructor;
import com.sad.myadvice.adminEntity.Room;
import com.sad.myadvice.adminEntity.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByCourse(AdminCourse course);
    List<Section> findByInstructor(Instructor instructor);
    List<Section> findByRoom(Room room);
}

