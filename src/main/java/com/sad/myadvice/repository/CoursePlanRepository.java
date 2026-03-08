package com.sad.myadvice.repository;

import com.sad.myadvice.entity.CoursePlan;
import com.sad.myadvice.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CoursePlanRepository extends JpaRepository<CoursePlan, Long> {
    List<CoursePlan> findByStudent(User student);
    List<CoursePlan> findByStudentAndIsApproved(User student, boolean isApproved);
}
