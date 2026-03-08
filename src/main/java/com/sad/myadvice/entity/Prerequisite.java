package com.sad.myadvice.entity;

import jakarta.persistence.*;

//----Maps courses to their pre/co/antirequisites---

@Entity
@Table(name = "prerequisite")
public class Prerequisite {

    @Id //(Primary key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto-increments id every time new user is added
    private Long id;

    //The course that HAS the requirement(s)
    //Stored in column "course_id"
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    //The course that has to be taken first
    //Stored in column "required_course_id"
    @ManyToOne
    @JoinColumn(name = "required_course_id", nullable = false)
    private Course requiredCourse;

    @Enumerated(EnumType.STRING)
    private Type type;

    //
    public enum Type {
        PRE, //prerequisite
        CO, //corequisite
        ANTI //antirequisite
    }

    //----Getters and setters----
    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public Course getRequiredCourse() { return requiredCourse; }
    public void setRequiredCourse(Course requiredCourse) { this.requiredCourse = requiredCourse; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
}