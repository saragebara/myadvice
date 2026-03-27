package com.sad.myadvice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    private User faculty;

    private LocalDateTime dateTime;//the requested date/time
    private String note;//student note for appointment

    @Enumerated(EnumType.STRING)
    private ReasonType reasonType;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum ReasonType {
        CURRICULUM, COMP400, RESEARCH, GENERAL
    }

    public enum Status {
        PENDING,     //student booked, waiting for faculty
        CONFIRMED,   //faculty accepted
        REJECTED,    //faculty rejected
        CANCELLED,   //student cancelled
        COMPLETED    //appointment happened
    }

    //Getters and setters ---------------------------------------------------------
    //id
    public Long getId() { return id; }
    //student
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    //faculty
    public User getFaculty() { return faculty; }
    public void setFaculty(User faculty) { this.faculty = faculty; }
    //date/time
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    //appointment note
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    //appointment reason
    public ReasonType getReasonType() { return reasonType; }
    public void setReasonType(ReasonType reasonType) { this.reasonType = reasonType; }
    //apointment status
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}