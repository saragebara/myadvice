package com.sad.myadvice.advising.model;

import java.time.LocalDate;

public class BookingRequest {
    private final String requesterRole;
    private final String studentName;
    private final String studentId;
    private final String purpose;
    private final LocalDate preferredDate;
    private final String preferredTime;
    private final String details;

    public BookingRequest(String requesterRole, String studentName, String studentId,
                          String purpose, LocalDate preferredDate, String preferredTime,
                          String details) {
        this.requesterRole = requesterRole;
        this.studentName = studentName;
        this.studentId = studentId;
        this.purpose = purpose;
        this.preferredDate = preferredDate;
        this.preferredTime = preferredTime;
        this.details = details;
    }

    public String getRequesterRole() {
        return requesterRole;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getPurpose() {
        return purpose;
    }

    public LocalDate getPreferredDate() {
        return preferredDate;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public String getDetails() {
        return details;
    }
}
