package com.sad.myadvice.administering.service;

import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.Instructor;
import com.sad.myadvice.entity.Room;
import com.sad.myadvice.entity.Section;
import com.sad.myadvice.repository.CourseRepository;
import com.sad.myadvice.repository.InstructorRepository;
import com.sad.myadvice.repository.RoomRepository;
import com.sad.myadvice.repository.SectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Uses Course instead of AdminCourse — they mapped to the same table.
 * Removed the duplicate @Autowired field that was alongside the constructor injection.
 */
@Service
public class TimetableService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final RoomRepository roomRepository;

    public TimetableService(SectionRepository sectionRepository,
                            CourseRepository courseRepository,
                            InstructorRepository instructorRepository,
                            RoomRepository roomRepository) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
        this.roomRepository = roomRepository;
    }

    // --- Sections ---

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public Section getSectionById(Long id) {
        return sectionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Section not found: " + id));
    }

    public Section createSection(Long courseId, Long instructorId,
                                  Long roomId, Section sectionDetails) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));
        Instructor instructor = instructorRepository.findById(instructorId)
            .orElseThrow(() -> new RuntimeException("Instructor not found: " + instructorId));
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));

        sectionDetails.setCourse(course);
        sectionDetails.setInstructor(instructor);
        sectionDetails.setRoom(room);
        return sectionRepository.save(sectionDetails);
    }

    public Section updateSection(Long id, Section updated) {
        Section existing = getSectionById(id);
        existing.setCourse(updated.getCourse());
        existing.setInstructor(updated.getInstructor());
        existing.setRoom(updated.getRoom());
        existing.setSectionNumber(updated.getSectionNumber());
        existing.setDay(updated.getDay());
        existing.setTime(updated.getTime());
        return sectionRepository.save(existing);
    }

    public void deleteSection(Long id) {
        sectionRepository.delete(getSectionById(id));
    }

    public List<Section> getSectionsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));
        return sectionRepository.findByCourse(course);
    }

    public List<Section> getSectionsByInstructor(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
            .orElseThrow(() -> new RuntimeException("Instructor not found: " + instructorId));
        return sectionRepository.findByInstructor(instructor);
    }

    public List<Section> getSectionsByRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
        return sectionRepository.findByRoom(room);
    }

    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}