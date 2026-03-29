package com.sad.myadvice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "schedule")
public class Schedule {

    @Id //+1 every time generated
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    private String scheduleName; //ex 2026W Schedule
    private String term; //ex "2026W"
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<ScheduleItem> items;

    //id
    public Long getId() { return id; }
    //student
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    //sched name
    public String getScheduleName() { return scheduleName; }
    public void setScheduleName(String scheduleName) { this.scheduleName = scheduleName; }
    //sched term
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    //date and time schedule was created
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    //schedule items
    public List<ScheduleItem> getItems() { return items; }
    public void setItems(List<ScheduleItem> items) { this.items = items; }
}