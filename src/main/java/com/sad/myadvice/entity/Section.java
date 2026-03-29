package com.sad.myadvice.adminEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sectionNumber;

    private String day; //(monday...)
    private String time; 

    //Link to course
    @ManyToOne
    @JoinColumn(name = "course_id")
    private AdminCourse course;

    //Link to Faculty
    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    //Link to Room
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    //getters and setters
    //id
    public Long getId() { return id; }
    //section number
    public String getSectionNumber() { return sectionNumber; }
    public void setSectionNumber(String sectionNumber) { this.sectionNumber = sectionNumber; }
    //day
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    //time
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    //course
    public AdminCourse getCourse() { return course; }
    public void setCourse(AdminCourse course) { this.course = course; }
    //instructor
    public Instructor getInstructor() { return instructor; }
    public void setInstructor(Instructor instructor) { this.instructor = instructor; }
    //room
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
}
