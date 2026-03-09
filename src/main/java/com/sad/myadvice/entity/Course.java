package com.sad.myadvice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "course")
public class Course {

    @Id //(Primary key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto-increments id every time new user is added
    private Long id;

    private String code; //for ex, "COMP2800"
    private String name; //"Software Development"
    private int credits; //# of credits
    private int yearLevel; //1, 2, 3, or 4
    private boolean isRequired; //whether it's a required course or not

    @Enumerated(EnumType.STRING)
    private Category category;

    //stores the category of the course. will edit this later to include all requirements for different CS majors
    public enum Category { 
        CORE, ART, SOCIAL_SCIENCE, MATH, FREE_ELECTIVE
    }

    //----Getters and setters----
    //id
    public Long getId() { return id; }
    //course code
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    //course name
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    //course credits
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    //year levels
    public int getYearLevel() { return yearLevel; }
    public void setYearLevel(int yearLevel) { this.yearLevel = yearLevel; }
    //course requirement
    public boolean isRequired() { return isRequired; }
    public void setRequired(boolean required) { isRequired = required; }
    //course category
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}