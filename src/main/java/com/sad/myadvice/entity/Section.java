package com.sad.myadvice.entity;

import jakarta.persistence.*;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sectionNumber;
    private String day;
    private String time;

    // FIX: was AdminCourse — now uses the shared Course entity (same course table)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id")
    private Room room;

    public Long getId() { return id; }
    public String getSectionNumber() { return sectionNumber; }
    public void setSectionNumber(String sectionNumber) { this.sectionNumber = sectionNumber; }
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public Instructor getInstructor() { return instructor; }
    public void setInstructor(Instructor instructor) { this.instructor = instructor; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
}