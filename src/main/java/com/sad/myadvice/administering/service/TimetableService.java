package com.sad.myadvice.administering.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sad.myadvice.entity.AdminCourse;
import com.sad.myadvice.entity.Instructor;
import com.sad.myadvice.entity.Room;
import com.sad.myadvice.entity.Section;
import com.sad.myadvice.repository.AdminCourseRepository;
import com.sad.myadvice.repository.InstructorRepository;
import com.sad.myadvice.repository.RoomRepository;
import com.sad.myadvice.repository.SectionRepository;

@Service
public class TimetableService {
    private final SectionRepository sectionRepository;
    private final AdminCourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final RoomRepository roomRepository;

    public TimetableService(SectionRepository sectionRepository, AdminCourseRepository courseRepository,
                            InstructorRepository instructorRepository, RoomRepository roomRepository) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
        this.roomRepository = roomRepository;
    }

    // Get all sections
    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    // Get section by id
    public Section getSectionById(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));
    }

    // Create a section
    public Section createSection(Long courseId, Long instructorId, Long roomId, Section sectionDetails) {
        AdminCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        sectionDetails.setCourse(course);
        sectionDetails.setInstructor(instructor);
        sectionDetails.setRoom(room);

        return sectionRepository.save(sectionDetails);
    }

    // Update section
    public Section updateSection(Long id, Section updatedSection) {
        Section existing = getSectionById(id);
        existing.setSectionNumber(updatedSection.getSectionNumber());
        existing.setDay(updatedSection.getDay());
        existing.setTime(updatedSection.getTime());
        // Optionally update course, instructor, room if needed
        return sectionRepository.save(existing);
    }

    // Delete section
    public void deleteSection(Long id) {
        Section section = getSectionById(id);
        sectionRepository.delete(section);
    }

    // Get sections by course
    public List<Section> getSectionsByCourse(Long courseId) {
        AdminCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return sectionRepository.findByCourse(course);
    }

    // Get sections by instructor
    public List<Section> getSectionsByInstructor(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        return sectionRepository.findByInstructor(instructor);
    }

    // Get sections by room
    public List<Section> getSectionsByRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return sectionRepository.findByRoom(room);
    }
}