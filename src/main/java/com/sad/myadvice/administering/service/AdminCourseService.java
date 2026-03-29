package com.sad.myadvice.administering.service;

import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.Prerequisite;
import com.sad.myadvice.repository.CourseRepository;
import com.sad.myadvice.repository.PrerequisiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Uses the shared Course and Prerequisite entities instead of AdminCourse 
 * both point to the same DB tables
 */
@Service
public class AdminCourseService {

    private final CourseRepository courseRepository;
    private final PrerequisiteRepository prerequisiteRepository;

    public AdminCourseService(CourseRepository courseRepository,
                              PrerequisiteRepository prerequisiteRepository) {
        this.courseRepository = courseRepository;
        this.prerequisiteRepository = prerequisiteRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Course not found: " + id));
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course updated) {
        Course existing = getCourseById(id);
        existing.setCode(updated.getCode());
        existing.setName(updated.getName());
        existing.setCredits(updated.getCredits());
        existing.setYearLevel(updated.getYearLevel());
        existing.setRequired(updated.isRequired());
        existing.setCategory(updated.getCategory());
        existing.setDescription(updated.getDescription());
        existing.setOfferedFall(updated.isOfferedFall());
        existing.setOfferedWinter(updated.isOfferedWinter());
        existing.setOfferedSummer(updated.isOfferedSummer());
        return courseRepository.save(existing);
    }

    public void deleteCourse(Long id) {
        //remove all prerequisite rows that reference this course first to avoid FK constraint violations
        List<Prerequisite> asMain = prerequisiteRepository.findByCourse(getCourseById(id));
        List<Prerequisite> asRequired = prerequisiteRepository.findByRequiredCourse(getCourseById(id));
        prerequisiteRepository.deleteAll(asMain);
        prerequisiteRepository.deleteAll(asRequired);
        courseRepository.deleteById(id);
    }

    //Prerequisite management
    //Uses the existing prerequisite table via PrerequisiteRepository

    public List<Prerequisite> getPrerequisitesForCourse(Long courseId) {
        Course course = getCourseById(courseId);
        return prerequisiteRepository.findByCourse(course);
    }

    public Prerequisite addPrerequisite(Long courseId, Long prereqCourseId,
                                        Prerequisite.Type type) {
        if (courseId.equals(prereqCourseId)) {
            throw new RuntimeException("A course cannot be its own prerequisite.");
        }
        Course course = getCourseById(courseId);
        Course requiredCourse = getCourseById(prereqCourseId);

        // Avoid duplicates
        boolean alreadyExists = prerequisiteRepository.findByCourse(course).stream()
            .anyMatch(p -> p.getRequiredCourse().getId().equals(prereqCourseId)
                       && p.getType() == type);
        if (alreadyExists) {
            throw new RuntimeException("This prerequisite relationship already exists.");
        }

        Prerequisite prereq = new Prerequisite();
        prereq.setCourse(course);
        prereq.setRequiredCourse(requiredCourse);
        prereq.setType(type);
        return prerequisiteRepository.save(prereq);
    }

    public void removePrerequisite(Long prereqRowId) {
        prerequisiteRepository.deleteById(prereqRowId);
    }
}