package com.sad.myadvice.advising.service;

import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.CoursePlan;
import com.sad.myadvice.entity.CoursePlanItem;
import com.sad.myadvice.entity.Prerequisite;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.CoursePlanItemRepository;
import com.sad.myadvice.repository.CoursePlanRepository;
import com.sad.myadvice.repository.PrerequisiteRepository;
import com.sad.myadvice.repository.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CoursePlanService {
    private final CoursePlanRepository coursePlanRepository;
    private final CoursePlanItemRepository coursePlanItemRepository;
    private final CurriculumService curriculumService;
    private final TranscriptService transcriptService;
    private final PrerequisiteRepository prerequisiteRepository;
    private final SectionRepository sectionRepository;

    public CoursePlanService(
            CoursePlanRepository coursePlanRepository,
            CoursePlanItemRepository coursePlanItemRepository,
            CurriculumService curriculumService,
            TranscriptService transcriptService,
            PrerequisiteRepository prerequisiteRepository,
            SectionRepository sectionRepository) {
        this.coursePlanRepository = coursePlanRepository;
        this.coursePlanItemRepository = coursePlanItemRepository;
        this.curriculumService = curriculumService;
        this.transcriptService = transcriptService;
        this.prerequisiteRepository = prerequisiteRepository;
        this.sectionRepository = sectionRepository;
    }

    @PersistenceContext
    private EntityManager entityManager;

    public CoursePlan createPlan(User student, String planName) {
        CoursePlan plan = new CoursePlan();
        plan.setStudent(student);
        plan.setPlanName(planName);
        plan.setCreatedAt(java.time.LocalDateTime.now());
        plan.setApproved(false);
        plan.setItems(new ArrayList<>());
        return coursePlanRepository.save(plan);
    }

    //returns fresh DB copy so callers have up-to-date items list
    @Transactional
    public CoursePlan savePlan(CoursePlan plan) {
        if (plan.getItems() != null) {
            plan.getItems().forEach(item -> item.setPlan(plan));
        }
        CoursePlan saved = coursePlanRepository.save(plan);
        //forcing the initialization of the items list before returning to the UI
        saved.getItems().size();
        return saved;
    }

    @Transactional(readOnly = true)
    public CoursePlan getPlanById(Long planId) {
        CoursePlan plan = coursePlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Course plan not found: " + planId));
        
        //explicitly triggering the loading of the lazy collection while the session is open
        //(fixing error: Cannot lazily initialize collection of role 'com.sad.myadvice.entity.CoursePlan.items' with key '1' (no session))
        if (plan.getItems() != null) {
            plan.getItems().size(); 
        }
        
        return plan;
    }

    public List<CoursePlan> getPlansForStudent(User student) {
        return coursePlanRepository.findByStudent(student);
    }

    public List<CoursePlan> getApprovedPlans(User student) {
        return coursePlanRepository.findByStudentAndIsApproved(student, true);
    }

    public CoursePlan approvePlan(CoursePlan plan, User faculty) {
        plan.setApproved(true);
        plan.setApprovedBy(faculty);
        return coursePlanRepository.save(plan);
    }

    @Transactional
    public void deletePlan(CoursePlan plan) {
        //deleting items first to avoid FK constraint violations
        coursePlanItemRepository.deleteByPlan(plan);
        coursePlanRepository.delete(plan);
    }

    //returns the re-fetched plan to avoid errors
    public CoursePlan addItemToPlan(CoursePlan plan, Course course,
                                     String plannedTerm, int plannedYear) {
        //reload plan from DB to get current items list (avoids getting incorrect state)
        CoursePlan freshPlan = getPlanById(plan.getId());

        if (freshPlan.getItems() == null) {
            freshPlan.setItems(new ArrayList<>());
        }

        CoursePlanItem item = new CoursePlanItem();
        item.setPlan(freshPlan);
        item.setCourse(course);
        item.setPlannedTerm(plannedTerm);
        item.setPlannedYear(plannedYear);
        freshPlan.getItems().add(item);
        return savePlan(freshPlan);
    }

    @Transactional  //wraps delete + re-fetch in one transaction
    public CoursePlan removeItemFromPlan(CoursePlan plan, int itemIndex) {
        CoursePlan freshPlan = getPlanById(plan.getId());
        List<CoursePlanItem> items = freshPlan.getItems();
        if (items == null || itemIndex < 0 || itemIndex >= items.size()) {
            throw new RuntimeException("Invalid item index: " + itemIndex);
        }
        CoursePlanItem toRemove = items.get(itemIndex);
        items.remove(itemIndex);
        if (toRemove.getId() != null) {
            coursePlanItemRepository.deleteById(toRemove.getId());
            entityManager.flush(); //forces delete to DB before re-fetch
            entityManager.clear(); //clears 1st-level cache so re-fetch hits DB fresh
        }
        return getPlanById(freshPlan.getId());
    }

    //check if a course has any section in the timetable (i.e. is currently offered)
    public boolean isCourseOfferedInTimetable(Course course) {
        return !sectionRepository.findByCourse(course).isEmpty();
    }

    //Validation----------

    public boolean validatePlan(CoursePlan plan) {
        return getValidationMessages(plan).isEmpty();
    }

    public List<String> getValidationMessages(CoursePlan plan) {
        List<String> errors = new ArrayList<>();

        if (plan == null) {
            errors.add("Course plan is missing.");
            return errors;
        }

        //working with a fresh copy from DB to avoid stale item references
        CoursePlan freshPlan = getPlanById(plan.getId());

        if (freshPlan.getItems() == null || freshPlan.getItems().isEmpty()) {
            errors.add("Course plan contains no courses.");
            return errors;
        }

        User student = freshPlan.getStudent();
        if (student == null) {
            errors.add("Course plan must be associated with a student.");
            return errors;
        }

        List<CoursePlanItem> sortedItems = sortPlanItems(freshPlan.getItems());

        Set<String> completedCourseCodes = transcriptService.getCompletedCourses(student).stream()
            .map(Course::getCode)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        //all codes planned (used for antirequisite check)
        Set<String> plannedCourseCodes = sortedItems.stream()
            .map(CoursePlanItem::getCourse)
            .filter(Objects::nonNull)
            .map(Course::getCode)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<String> duplicateTracker = new HashSet<>();

        for (CoursePlanItem item : sortedItems) {
            Course course = item.getCourse();
            if (course == null) {
                errors.add("A plan item is missing its course.");
                continue;
            }

            String courseCode = course.getCode();
            if (courseCode == null || courseCode.isBlank()) {
                errors.add("A plan item references a course with no code.");
                continue;
            }

            //dupes check
            if (!duplicateTracker.add(courseCode)) {
                errors.add("Duplicate course in plan: " + courseCode + ".");
                continue;
            }

            //if already completed, warn user - no need to plan it
            if (completedCourseCodes.contains(courseCode)) {
                errors.add(courseCode + " is already completed — remove it from the plan.");
                continue;
            }

            //prereq ordering check
            List<Prerequisite> prerequisites = prerequisiteRepository.findByCourse(course);
            for (Prerequisite prereq : prerequisites) {
                Course requiredCourse = prereq.getRequiredCourse();
                if (requiredCourse == null) continue;
                String requiredCode = requiredCourse.getCode();
                if (requiredCode == null || requiredCode.isBlank()) continue;

                switch (prereq.getType()) {
                    case PRE -> {
                        //Must be completed already OR planned in an earlier term
                        if (!completedCourseCodes.contains(requiredCode)
                                && !isCoursePlannedBefore(requiredCode, item, sortedItems)) {
                            errors.add(courseCode + " requires " + requiredCode
                                + " to be completed before it.");
                        }
                    }
                    case CO -> {
                        //Must be completed already OR planned in same/earlier term
                        if (!completedCourseCodes.contains(requiredCode)
                                && !isCoursePlannedOnOrBefore(requiredCode, item, sortedItems)) {
                            errors.add(courseCode + " requires corequisite " + requiredCode
                                + " in the same or earlier term.");
                        }
                    }
                    case ANTI -> {
                        //Must NOT be completed or planned anywhere
                        if (completedCourseCodes.contains(requiredCode)
                                || plannedCourseCodes.contains(requiredCode)) {
                            errors.add(courseCode + " conflicts with " + requiredCode
                                + " (antirequisite) — both cannot be taken.");
                        }
                    }
                }
            }
        }

        //missing required courses for major
        List<Course> missingRequired = getMissingRequiredCourses(freshPlan);
        if (!missingRequired.isEmpty()) {
            String missingCodes = missingRequired.stream()
                .map(Course::getCode)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
            errors.add("Missing required courses for your major: " + missingCodes + ".");
        }

        return errors;
    }

    public List<Course> getMissingRequiredCourses(CoursePlan plan) {
        if (plan == null || plan.getStudent() == null) return List.of();

        Set<String> completedCodes = transcriptService.getCompletedCourses(plan.getStudent()).stream()
            .map(Course::getCode).filter(Objects::nonNull).collect(Collectors.toSet());

        Set<String> plannedCodes = plan.getItems() == null ? Set.of() : plan.getItems().stream()
            .map(CoursePlanItem::getCourse).filter(Objects::nonNull)
            .map(Course::getCode).filter(Objects::nonNull).collect(Collectors.toSet());

        return curriculumService.getRequiredCoursesForMajor(plan.getStudent()).stream()
            .filter(c -> !completedCodes.contains(c.getCode())
                      && !plannedCodes.contains(c.getCode()))
            .toList();
    }

    //sorting helpers ----------

    private List<CoursePlanItem> sortPlanItems(List<CoursePlanItem> items) {
        return items.stream().sorted(this::comparePlanOrder).toList();
    }

    private int comparePlanOrder(CoursePlanItem a, CoursePlanItem b) {
        int aYear = (a == null || a.getPlannedYear() <= 0) ? Integer.MAX_VALUE : a.getPlannedYear();
        int bYear = (b == null || b.getPlannedYear() <= 0) ? Integer.MAX_VALUE : b.getPlannedYear();
        if (aYear != bYear) return Integer.compare(aYear, bYear);
        return Integer.compare(
            getTermOrder(a == null ? null : a.getPlannedTerm()),
            getTermOrder(b == null ? null : b.getPlannedTerm())
        );
    }

    private int getTermOrder(String term) {
        if (term == null || term.isBlank()) return Integer.MAX_VALUE;
        String n = term.trim().toLowerCase();
        if (n.contains("winter") || n.endsWith("w"))  return 1;
        if (n.contains("spring") || n.endsWith("s"))  return 2;
        if (n.contains("summer") || n.endsWith("su")) return 3;
        if (n.contains("fall")   || n.contains("autumn") || n.endsWith("f")) return 4;
        return Integer.MAX_VALUE;
    }

    private boolean isCoursePlannedBefore(String code, CoursePlanItem current,
                                           List<CoursePlanItem> sorted) {
        return sorted.stream()
            .filter(i -> i != null && i != current && comparePlanOrder(i, current) < 0)
            .map(CoursePlanItem::getCourse).filter(Objects::nonNull)
            .map(Course::getCode)
            .anyMatch(c -> c != null && c.equalsIgnoreCase(code));
    }

    private boolean isCoursePlannedOnOrBefore(String code, CoursePlanItem current,
                                               List<CoursePlanItem> sorted) {
        return sorted.stream()
            .filter(i -> i != null && comparePlanOrder(i, current) <= 0)
            .map(CoursePlanItem::getCourse).filter(Objects::nonNull)
            .map(Course::getCode)
            .anyMatch(c -> c != null && c.equalsIgnoreCase(code));
    }
}