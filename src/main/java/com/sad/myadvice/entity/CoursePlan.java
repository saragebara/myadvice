package com.sad.myadvice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "course_plan")
public class CoursePlan {

    @Id //(Primary key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto-increments id every time new user is added
    private Long id;

    //many to one relationship with students
    @ManyToOne //each student can have multiple course plans
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    //many to one relationship with faculty
    @ManyToOne //each plan can be approved by a faculty member
    @JoinColumn(name = "approved_by")
    private User approvedBy; //refers to the faculty who approved the plan, null if not approved

    private String planName; //ex "4 year plan"
    private LocalDateTime createdAt; //creation date
    private boolean isApproved; //whether or not its been approved by a faculty member

    //one to many relationship with plan items - one plan can have many items
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL) //cascade type makes it so that deleting a plan deletes all items too
    private List<CoursePlanItem> items;

    //----Getters and setters----
    public Long getId() { return id; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public User getApprovedBy() { return approvedBy; }
    public void setApprovedBy(User approvedBy) { this.approvedBy = approvedBy; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }
    public List<CoursePlanItem> getItems() { return items; }
    public void setItems(List<CoursePlanItem> items) { this.items = items; }
}