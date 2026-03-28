package com.sad.myadvice.adminEntity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "course")
public class AdminCourse {
    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto-increments id every time new user is added
    private Long id;

    private String courseCode; 
    private String CourseName;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program; //the program this course belongs to

    //many to many relationship with itself to represent prerequisites
    //(many courses can have many prerequisites, and a course can be a prerequisite for many courses)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "course_prerequisites",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "prerequisite_id")
    )
    private List<AdminCourse> prerequisites; //courses that are prerequisites for this course
    //getters and setters
    //id
    public Long getId() { return id; }
    //code
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    //name
    public String getCourseName() { return CourseName; }
    public void setCourseName(String CourseName) { this.CourseName = CourseName; }
    //program
    public Program getProgram() { return program; }
    public void setProgram(Program program) { this.program = program; }
    //prerequisites
    public List<AdminCourse> getPrerequisites() { return prerequisites; }
    public void setPrerequisites(List<AdminCourse> prerequisites) { this.prerequisites = prerequisites; }
}
