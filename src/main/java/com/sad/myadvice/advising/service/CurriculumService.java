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
    public List<Course> getRequiredCoursesForMajor(User student){
        if (student.getMajor() == null) {
            //if student somehow doesn't have a major then fallback 
            return courseRepository.findByIsRequired(true);
        }
        //map courses based on student's major. return list
        return courseProgramRepository.findByMajor(student.getMajor())
            .stream().map(CourseProgram::getCourse).toList();
    }

    //get the remaining courses that a student needs for degree reqs
    public List<Course> getRemainingRequiredCourses(User student){
        List<Course> required = getRequiredCoursesForMajor(student);
        List<Course> completed = transcriptService.getCompletedCourses(student);
        //returning courses in the degree reqs that are NOT yet completed (in transcript)
        return required.stream().filter(c -> !completed.contains(c)).toList();
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
        if (student.getMajor() == null) {
            return course.isRequired(); //fallback to global flag in case of error
        }
        return courseProgramRepository.findByMajor(student.getMajor())
            .stream().anyMatch(cp -> cp.getCourse().equals(course));
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

    //Get all the required courses for specific major that a student hasn't taken yet that they are ABLE to take
    public List<Course> getEligibleRequired(User student) {
        List<Course> required = getRequiredCoursesForMajor(student);
        List<Course> completed = transcriptService.getCompletedCourses(student);
        List<Course> inProgress = transcriptService.getInProgressCourses(student);

        return required.stream()
            .filter(c -> !completed.contains(c)) //not completed
            .filter(c -> !inProgress.contains(c)) //not currently in progress
            .filter(c -> isEligible(student, c)) //is eligible to take
            .toList();
    }

    //checks all eligible electives a user can take 
    //bug fix: use isrRequiredForMajor instead of getEligibleCourses to filter properly
    public List<Course> getEligibleElectives(User student) {
        return getEligibleCourses(student).stream()
            .filter(c -> !isRequiredForMajor(student, c)).toList();
    }
}

