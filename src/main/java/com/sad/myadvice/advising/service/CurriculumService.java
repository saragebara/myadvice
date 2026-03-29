package com.sad.myadvice.advising.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.CourseProgram;
import com.sad.myadvice.entity.Prerequisite;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.CourseProgramRepository;
import com.sad.myadvice.repository.CourseRepository;
import com.sad.myadvice.repository.PrerequisiteRepository;

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

    //isEligible using a prefetched snapshot to avoid extra queries to DB
    public boolean isEligible(Course course, TranscriptService.TranscriptSnapshot snap, List<Prerequisite> prereqs) {
        for (Prerequisite prereq : prereqs) {
            Course required = prereq.getRequiredCourse();
            switch (prereq.getType()) {
                case PRE -> { if (!snap.isCompleted(required)) return false; }
                case CO -> { if (!snap.isCompletedOrInProgress(required)) return false; }
                case ANTI -> { if (snap.isCompletedOrInProgress(required)) return false; }
            }
        }
        return true;
    }

    //building the EligibleScreen in 3 queries total instead of the previous 100+ possibility
    //now uses 1. courseProgramRepository.findByMajor
    //2. transcriptRepository.findByStudent (snapshotted)
    //3. prerequisiteRepository.findAll (one bulk load)
    public EligibleBundle getEligibleBundle(User student) {
        List<Course> required = getRequiredCoursesForMajor(student); //query 1
        Set<Long> requiredIds = required.stream().map(Course::getId).collect(java.util.stream.Collectors.toSet());

        TranscriptService.TranscriptSnapshot snap = transcriptService.getSnapshot(student); //query 2

        //bulk-load ALL prerequisites once. query 3
        List<Prerequisite> allPrereqs = prerequisiteRepository.findAll();
        Map<Long, List<Prerequisite>> prereqsByCourseId = allPrereqs.stream()
            .collect(java.util.stream.Collectors.groupingBy(p -> p.getCourse().getId()));

        List<Course> allCourses = courseRepository.findAll();

        List<Course> eligibleRequired  = new ArrayList<>();
        List<Course> eligibleElectives = new ArrayList<>();

        for (Course c : allCourses) {
            if (snap.isCompleted(c) || snap.isInProgress(c)) continue;
            List<Prerequisite> prereqs = prereqsByCourseId.getOrDefault(c.getId(), List.of());
            if (!isEligible(c, snap, prereqs)) continue;

            if (requiredIds.contains(c.getId())) eligibleRequired.add(c);
            else eligibleElectives.add(c);
        }

        return new EligibleBundle(eligibleRequired, eligibleElectives);
    }

    //value object returned by getEligibleBundle
    public static class EligibleBundle {
        public final List<Course> eligibleRequired;
        public final List<Course> eligibleElectives;
        public EligibleBundle(List<Course> req, List<Course> elec) {
            this.eligibleRequired  = req;
            this.eligibleElectives = elec;
        }
    }

    //builds everything that progress screen needs
    //1.courseProgramRepository.findByMajor
    //2.transcriptRepository.findByStudent (via snapshot)
    //remaining + pct computed in memory
    public ProgressBundle getProgressBundle(User student) {
        List<Course> required = getRequiredCoursesForMajor(student);//query 1
        Set<Long> requiredIds = required.stream().map(Course::getId).collect(java.util.stream.Collectors.toSet());

        TranscriptService.TranscriptSnapshot snap = transcriptService.getSnapshot(student); //query 2

        List<Course> remaining = required.stream().filter(c -> !snap.isCompleted(c)).toList(); //pure in-memory

        long completedRequiredCount = requiredIds.stream()
            .filter(snap.completedIds::contains)
            .count();
        double pct = required.isEmpty() ? 0.0 : (completedRequiredCount * 100.0 / required.size());

        int completedCount  = (int) snap.completedIds.size();
        int inProgressCount = (int) snap.inProgressIds.size();
        return new ProgressBundle(remaining, pct, completedCount, inProgressCount);
    }

    //finally bundling it
    public static class ProgressBundle {
        public final List<Course> remaining;
        public final double completionPct;
        public final int completedCount;
        public final int inProgressCount;
        
        public ProgressBundle(List<Course> remaining, double completionPct, int completedCount, int inProgressCount) {
            this.remaining = remaining;
            this.completionPct = completionPct;
            this.completedCount = completedCount;
            this.inProgressCount = inProgressCount;
        }
    }

    //returns the set of course IDs required for the student's major in ONE query
    public Set<Long> getRequiredCourseIdsForMajor(User student) {
        return getRequiredCoursesForMajor(student).stream()
            .map(Course::getId)
            .collect(java.util.stream.Collectors.toSet());
    }


    //loads all data needed for populateDetailsPanel in 3 queries total:
    //1. courseProgramRepository.findByMajor (via getRequiredCoursesForMajor)
    //2. prerequisiteRepository.findByCourse (prereqs for this one selected course)
    //3. transcriptRepository.findByStudent(via snapshot)
    //isEligible and isRequiredForMajor are then computed in memory
    public CourseDetailBundle getCourseDetailBundle(User student, Course course, Set<Long> requiredIds) {
        //prereqs for this course
        List<Prerequisite> prereqs = prerequisiteRepository.findByCourse(course);
        //transcript snapshot
        TranscriptService.TranscriptSnapshot snap = transcriptService.getSnapshot(student);
        //isRequiredForMajor, in memory using passed-in set
        boolean requiredForMajor = requiredIds.contains(course.getId());
        //getRecommendedYear: in memory: re-use already-loaded program list
        int recommendedYear = course.getYearLevel(); //default
        //isEligible: in memory using snapshot
        boolean eligible = true;
        for (Prerequisite prereq : prereqs) {
            Course req = prereq.getRequiredCourse();
            eligible = switch (prereq.getType()) {
                case PRE-> snap.isCompleted(req);
                case CO -> snap.isCompletedOrInProgress(req);
                case ANTI -> !snap.isCompletedOrInProgress(req);
            };
            if (!eligible) break;
        }
        return new CourseDetailBundle(requiredForMajor, recommendedYear, eligible, prereqs);
    }

    public static class CourseDetailBundle {
        public final boolean requiredForMajor;
        public final int     recommendedYear;
        public final boolean eligible;
        public final List<Prerequisite> prereqs;
        public CourseDetailBundle(boolean requiredForMajor, int recommendedYear,
                                boolean eligible, List<Prerequisite> prereqs) {
            this.requiredForMajor = requiredForMajor;
            this.recommendedYear  = recommendedYear;
            this.eligible         = eligible;
            this.prereqs          = prereqs;
        }
    }
}

