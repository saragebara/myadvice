package com.sad.myadvice.advising.service;

import com.sad.myadvice.entity.CoursePlan;
import com.sad.myadvice.entity.CoursePlanItem;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.CoursePlanRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CoursePlanService {
    private final CoursePlanRepository coursePlanRepository;
    private final CurriculumService curriculumService;

    public CoursePlanService(CoursePlanRepository coursePlanRepository, CurriculumService curriculumService) {
        this.coursePlanRepository = coursePlanRepository;
        this.curriculumService = curriculumService;
    }

    //Create a new empty plan for specific student
    public CoursePlan createPlan(User student, String planName) {
        CoursePlan plan = new CoursePlan();
        plan.setStudent(student);
        plan.setPlanName(planName);
        plan.setCreatedAt(LocalDateTime.now()); //setting creation date
        plan.setApproved(false); //setting approval as false initially
        return coursePlanRepository.save(plan); //save() is used to write to the database
    }

    //Get all plans that a student has
    public List<CoursePlan> getPlansForStudent(User student) {
        return coursePlanRepository.findByStudent(student);
    }

    //Get only a student's plans that have been approved
    public List<CoursePlan> getApprovedPlans(User student) {
        return coursePlanRepository.findByStudentAndIsApproved(student, true);
    }

    //If a faculty member approves a plan
    public CoursePlan approvePlan(CoursePlan plan, User faculty) {
        plan.setApproved(true);
        plan.setApprovedBy(faculty);
        return coursePlanRepository.save(plan);
    }

    //Validate plan, currently it only checks if a student is eligible to take each course
    //TO-DO: add validation for the ordering of courses (prereqs should be satisfied in order)
    public boolean validatePlan(CoursePlan plan) {
        List<CoursePlanItem> items = plan.getItems();

        for (CoursePlanItem item : items) {
            if (!curriculumService.isEligible(plan.getStudent(), item.getCourse())) {
                return false;
            }
        }
        return true;
    }

    //Delete a plan
    public void deletePlan(CoursePlan plan) {
        coursePlanRepository.delete(plan);
    }
}