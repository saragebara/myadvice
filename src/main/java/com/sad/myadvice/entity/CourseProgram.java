package com.sad.myadvice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "course_program")
public class CourseProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id auto incremented with each tuple
    private Long id;

    @ManyToOne //many to one relationship with course ids
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "major") //major
    @Enumerated(EnumType.STRING)
    private Major major;

    private int recommendedYear;  //1, 2, 3, or 4 recommended year to take a course

    //----Getters and setters----
    //id
    public Long getId() { return id; }
    //course
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    //major
    public Major getMajor() { return major; }
    public void setMajor(Major major) { this.major = major; }
    //recommended year
    public int getRecommendedYear() { return recommendedYear; }
    public void setRecommendedYear(int year) { this.recommendedYear = year; }
}