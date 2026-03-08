package com.sad.myadvice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "transcript")
public class Transcript {

    @Id //(Primary key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto-increments id every time new user is added
    private Long id;

    //many to one relationship with students
    @ManyToOne //many transcripts can belong to a single student
    @JoinColumn(name = "user_id", nullable = false) //foreign key column in this table referencing a student's id (from user table)
    private User student;

    //many to one relationship with courses
    @ManyToOne //many transcript rows can belong to a single course
    @JoinColumn(name = "course_id", nullable = false) //fk column referencing a course_id from the course table
    private Course course;

    private String term; //for ex, "2026W"
    private Double grade; //grade percentage, null if in progress

    @Enumerated(EnumType.STRING)
    private Status status; //keeps track of each course's status

    public enum Status {
        COMPLETED, IN_PROGRESS, WITHDRAWN
    }

    //----Getters and setters----
    public Long getId() { return id; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public Double getGrade() { return grade; }
    public void setGrade(Double grade) { this.grade = grade; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}