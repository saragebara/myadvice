package com.sad.myadvice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "course_plan_item")
public class CoursePlanItem {

    @Id //(Primary key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto-increments id every time new user is added
    private Long id;

    //many to one relationship - many items can belong to a single plan
    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private CoursePlan plan; //the plan this item belongs to

    //many to one relationship with the course the user plans to take
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;  //course planned

    private String plannedTerm; //for ex, "2026F"
    private int plannedYear; //for ex, 2,3,4 (refers to year of study)

    //----Getters and setters----
    public Long getId() { return id; }
    public CoursePlan getPlan() { return plan; }
    public void setPlan(CoursePlan plan) { this.plan = plan; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public String getPlannedTerm() { return plannedTerm; }
    public void setPlannedTerm(String plannedTerm) { this.plannedTerm = plannedTerm; }
    public int getPlannedYear() { return plannedYear; }
    public void setPlannedYear(int plannedYear) { this.plannedYear = plannedYear; }
}