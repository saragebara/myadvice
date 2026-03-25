package com.sad.myadvice.advising.service;

import com.sad.myadvice.advising.model.AdvisorProfile;
import com.sad.myadvice.advising.model.BookingRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class BookingService {

    public List<AdvisorProfile> getAllAdvisors() {
        // TODO (Database): load advisor records from DB via repository/JDBC.
        // Example table: faculty_advisor(advisor_name, department, advising_topic,
        // availability_status, office_hours, active)
        List<AdvisorProfile> advisors = new ArrayList<>();
        advisors.add(new AdvisorProfile("Dr. A. Parker", "Computing Science", "Curriculum", "Available", "Mon/Wed 10:00-12:00"));
        advisors.add(new AdvisorProfile("Dr. S. Nguyen", "Computing Science", "Timetable", "Busy", "Tue/Thu 13:00-15:00"));
        advisors.add(new AdvisorProfile("Dr. M. Ibrahim", "Computing Science", "COMP 400/405", "Available", "Fri 09:30-12:30"));
        advisors.add(new AdvisorProfile("Dr. J. Lee", "Data Science", "Research", "Available", "Mon 14:00-17:00"));
        return advisors;
    }

    public List<AdvisorProfile> findAdvisorsByTopic(String topic) {
        List<AdvisorProfile> all = getAllAdvisors();
        if (topic == null || topic.isBlank() || "All Topics".equalsIgnoreCase(topic)) {
            return all;
        }

        String normalized = topic.toLowerCase(Locale.ROOT);
        return all.stream()
            .filter(a -> a.getAdvisingTopic().toLowerCase(Locale.ROOT).equals(normalized))
            .toList();
    }

    public String submitBooking(BookingRequest request) {
        // TODO (Database): persist booking request in DB and return generated id.
        // Example flow:
        // 1) validate request data
        // 2) INSERT into appointment_request table
        // 3) optionally INSERT appointment/advisor assignment record
        // 4) return generated booking id from DB
        String shortToken = UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        return "BK-" + shortToken;
    }
}
