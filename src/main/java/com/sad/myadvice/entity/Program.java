package com.sad.myadvice.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Program {
    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., "Computer Science"

    // @OneToMany(mappedBy = "program")
    // private List<Course> courses;

    //getters and setters
    //id
    public Long getId() { return id; }
    //name
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    //courses
    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}