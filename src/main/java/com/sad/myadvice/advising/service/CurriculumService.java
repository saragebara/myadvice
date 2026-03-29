package com.sad.myadvice.advising.service;

import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.CourseProgram;
import com.sad.myadvice.entity.Prerequisite;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.CourseProgramRepository;
import com.sad.myadvice.repository.CourseRepository;
import com.sad.myadvice.repository.PrerequisiteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Service
public class CurriculumService {
    private final CourseRepository courseRepository;
    private final PrerequisiteRepository prerequisiteRepository;
    private final CourseProgramRepository courseProgramRepository;
    private final TranscriptService transcriptService;

    public CurriculumService(CourseRepository courseRepository, PrerequisiteRepository prerequisiteRepository, TranscriptService transcriptService, CourseProgramRepository courseProgramRepository) {
        this.courseRepository = courseRepository;
        this.prerequisiteRepository = prerequisiteRepository;
        this.transcriptService = transcriptService;
        this.courseProgramRepository = courseProgramRepository;
    }

    //required courses for specific majors
    public List<Course> getRequiredCoursesForMajor(User student) {
        if (student == null || student.getMajor() == null) {
            return courseRepository.findByIsRequired(true);
        }
        return courseProgramRepository.findByMajor(student.getMajor())
            .stream()
            .map(CourseProgram::getCourse)
            .toList();
    }

    //get the remaining courses that a student needs for degree reqs
    public List<Course> getRemainingRequiredCourses(User student) {
        List<Course> required = getRequiredCoursesForMajor(student);
        List<Course> completed = transcriptService.getCompletedCourses(student);
        Set<Long> completedIds = completed.stream()
            .map(Course::getId)
            .collect(java.util.stream.Collectors.toSet());
        return required.stream()
            .filter(c -> !completedIds.contains(c.getId()))
            .toList();
    }


    //get the recommended year to take a course
    public int getRecommendedYear(User student, Course course){
        if(student.getMajor() == null) return course.getYearLevel();
        return courseProgramRepository.findByMajor(student.getMajor()).stream()
            .filter(cp-> cp.getCourse().equals(course))
            .map(CourseProgram::getRecommendedYear) //filtering and mapping program to reocmmended year for courses
            .findFirst()
            .orElse(course.getYearLevel());
    }

    //fixed logic -- required by major instead of global required flag implemented for MVP
    public boolean isRequiredForMajor(User student, Course course) {
        if (student == null || student.getMajor() == null) {
            return course.isRequired();
        }
        return courseProgramRepository.findByMajor(student.getMajor())
            .stream()
            .anyMatch(cp -> cp.getCourse().getId().equals(course.getId())); // compare by ID not object
    }

    //Check if a student is eligible for taking a specific course (has prereqs)
    public boolean isEligible(User student, Course course) {
        List<Prerequisite> prerequisites = prerequisiteRepository.findByCourse(course);

        for (Prerequisite prereq : prerequisites) {
            Course required = prereq.getRequiredCourse();

            switch (prereq.getType()) {
                case PRE -> {
                    //Case 1: If the course has a prereq, then the student HAS to have completed that course
                    if (!transcriptService.hasCompleted(student, required)) {
                        return false; //if they haven't, return fulse
                    }
                }
                case CO -> {
                    //Case 2: If the course has a coreq, then they had to have completed it OR are currently taking it
                    if (!transcriptService.hasCompletedOrInProgress(student, required)) {
                        return false; //if they haven't/aren't, return false
                    }
                }
                case ANTI -> {
                    //Case 3: If the course has an antirequisite, the student can't have taken/take it
                    if (transcriptService.hasCompletedOrInProgress(student, required)) {
                        return false; //if they have/are, return true
                    }
                }
            }
        }
        return true; //if passed cases 1-3 for EVERY prerequisite course then return true
    }

    //Get all courses that a student is currently eligible to take
    public List<Course> getEligibleCourses(User student) {
        List<Course> completed = transcriptService.getCompletedCourses(student);
        List<Course> inProgress = transcriptService.getInProgressCourses(student);
        Set<Long> completedIds = completed.stream().map(Course::getId)
            .collect(java.util.stream.Collectors.toSet());
        Set<Long> inProgressIds = inProgress.stream().map(Course::getId)
            .collect(java.util.stream.Collectors.toSet());

        return courseRepository.findAll().stream()
            .filter(c -> !completedIds.contains(c.getId()))
            .filter(c -> !inProgressIds.contains(c.getId()))
            .filter(c -> isEligible(student, c))
            .toList();
    }

    //Get all the required courses for specific major that a student hasn't taken yet that they are ABLE to take
    public List<Course> getEligibleRequired(User student) {
        List<Course> required = getRequiredCoursesForMajor(student);
        List<Course> completed = transcriptService.getCompletedCourses(student);
        List<Course> inProgress = transcriptService.getInProgressCourses(student);
        Set<Long> completedIds = completed.stream().map(Course::getId)
            .collect(java.util.stream.Collectors.toSet());
        Set<Long> inProgressIds = inProgress.stream().map(Course::getId)
            .collect(java.util.stream.Collectors.toSet());

        return required.stream()
            .filter(c -> !completedIds.contains(c.getId()))
            .filter(c -> !inProgressIds.contains(c.getId()))
            .filter(c -> isEligible(student, c))
            .toList();
    }

    //checks all eligible electives a user can take 
    //bug fix: use isrRequiredForMajor instead of getEligibleCourses to filter properly
    public List<Course> getEligibleElectives(User student) {
        List<Course> required = getRequiredCoursesForMajor(student);
        return getEligibleCourses(student).stream()
            .filter(c -> !required.contains(c))  // strictly exclude ALL required courses
            .toList();
    }
}

