package com.sad.myadvice.adminServices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sad.myadvice.adminEntity.AdminCourse;
import com.sad.myadvice.adminEntity.Instructor;
import com.sad.myadvice.adminEntity.Room;
import com.sad.myadvice.adminEntity.Section;
import com.sad.myadvice.adminRepo.AdminCourseRepository;
import com.sad.myadvice.adminRepo.InstructorRepository;
import com.sad.myadvice.adminRepo.RoomRepository;
import com.sad.myadvice.adminRepo.SectionRepository;

@Service
public class TimetableService {
    private final SectionRepository sectionRepository;
    @Autowired
    private AdminCourseRepository courseRepository;

    public AdminCourse updateCourse(AdminCourse course) {
        return courseRepository.save(course);
    }
    private final InstructorRepository instructorRepository;
    private final RoomRepository roomRepository;

    public TimetableService(SectionRepository sectionRepository, AdminCourseRepository courseRepository,
                            InstructorRepository instructorRepository, RoomRepository roomRepository) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
        this.roomRepository = roomRepository;
    }

    //get sections
    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }
    public Section getSectionById(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));
    }

    //create section
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

    //update section
    public Section updateSection(Long id, Section updatedSection) {
        Section existing = sectionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Section not found"));
        existing.setCourse(updatedSection.getCourse());
        existing.setInstructor(updatedSection.getInstructor());
        existing.setRoom(updatedSection.getRoom());
        existing.setSectionNumber(updatedSection.getSectionNumber());
        existing.setDay(updatedSection.getDay());
        existing.setTime(updatedSection.getTime());

        return sectionRepository.save(existing);
    }

    //delete
    public void deleteSection(Long id) {
        Section section = getSectionById(id);
        sectionRepository.delete(section);
    }

    //get sections by course, instructor, room
    public List<Section> getSectionsByCourse(Long courseId) {
        AdminCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return sectionRepository.findByCourse(course);
    }
    public List<Section> getSectionsByInstructor(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        return sectionRepository.findByInstructor(instructor);
    }
    public List<Section> getSectionsByRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return sectionRepository.findByRoom(room);
    }

    public List<Instructor> getAllInstructors() {
    return instructorRepository.findAll();
}
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}