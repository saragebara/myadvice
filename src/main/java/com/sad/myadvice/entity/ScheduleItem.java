package com.sad.myadvice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "schedule_item")
public class ScheduleItem {
    @Id //+1 with each addition
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //each schedule can have many schedule items
    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false) //can't be null
    private Schedule schedule;

    //each course can be on many schedules
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private String sectionCode; //ex"A", "B"
    private String instructor; //ex "Dr. Ahmed"
    private String dayTime; //ex "Mon 09:30-10:50"
    private String location; //ex "Erie Hall 210"

    //id
    public Long getId() { return id; }
    //schedule
    public Schedule getSchedule() { return schedule; }
    public void setSchedule(Schedule schedule) { this.schedule = schedule; }
    //course
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    //section
    public String getSectionCode() { return sectionCode; }
    public void setSectionCode(String sectionCode) { this.sectionCode = sectionCode; }
    //instructor
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    //course time
    public String getDayTime() { return dayTime; }
    public void setDayTime(String dayTime) { this.dayTime = dayTime; }
    //building and room number
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}