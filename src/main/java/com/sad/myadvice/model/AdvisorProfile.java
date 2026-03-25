package com.sad.myadvice.advising.model;

public class AdvisorProfile {
    private final String advisorName;
    private final String department;
    private final String advisingTopic;
    private final String availability;
    private final String officeHours;

    public AdvisorProfile(String advisorName, String department, String advisingTopic,
                          String availability, String officeHours) {
        this.advisorName = advisorName;
        this.department = department;
        this.advisingTopic = advisingTopic;
        this.availability = availability;
        this.officeHours = officeHours;
    }

    public String getAdvisorName() {
        return advisorName;
    }

    public String getDepartment() {
        return department;
    }

    public String getAdvisingTopic() {
        return advisingTopic;
    }

    public String getAvailability() {
        return availability;
    }

    public String getOfficeHours() {
        return officeHours;
    }
}
