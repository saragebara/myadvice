package com.sad.myadvice.repository;

import com.sad.myadvice.entity.CoursePlan;
import com.sad.myadvice.entity.CoursePlanItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoursePlanItemRepository extends JpaRepository<CoursePlanItem, Long> {
    List<CoursePlanItem> findByPlan(CoursePlan plan);
    void deleteByPlan(CoursePlan plan);
}