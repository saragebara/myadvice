package com.sad.myadvice.advising.service;

import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.Prerequisite;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.CourseRepository;
import com.sad.myadvice.repository.PrerequisiteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CurriculumService {
    private final CourseRepository courseRepository;
    private final PrerequisiteRepository prerequisiteRepository;
    private final TranscriptService transcriptService;

    public CurriculumService(CourseRepository courseRepository, PrerequisiteRepository prerequisiteRepository, TranscriptService transcriptService) {
        this.courseRepository = courseRepository;
        this.prerequisiteRepository = prerequisiteRepository;
        this.transcriptService = transcriptService;
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

        return courseRepository.findAll().stream() //to stream to filter
            .filter(c -> !completed.contains(c)) //course hasn't already been completed
            .filter(c -> !inProgress.contains(c)) //student isn't currently taking it
            .filter(c -> isEligible(student, c)) //all prerequisites are met
            .toList(); //back to list
    }

    //Get all the required courses that a student still has to take
    public List<Course> getRemainingRequiredCourses(User student) {
        List<Course> completed = transcriptService.getCompletedCourses(student);

        return courseRepository.findByIsRequired(true).stream().
        filter(c -> !completed.contains(c)).toList(); //filters to required courses that haven't been completed
    }

    //Get all the required courses that a student hasn't taken yet that they are ABLE to take
    public List<Course> getEligibleRequired(User student) {
        //filters by eligible courses then filters required eligible courses
        return getEligibleCourses(student).stream().filter(c -> c.isRequired()).toList();
    }

    //Gets all eligible electives courses a student can take
    public List<Course> getEligibleElectives(User student) {
        //same as getEligibleRequired except course is NOT required
        return getEligibleCourses(student).stream().filter(c -> !c.isRequired()).toList();
    }
}