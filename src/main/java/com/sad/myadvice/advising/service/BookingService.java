package com.sad.myadvice.advising.service;

import com.sad.myadvice.advising.model.AdvisorProfile;
import com.sad.myadvice.advising.model.BookingRequest;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private static final String FORM_ERROR_FOOTER = "Please check your entries and try again.";
    private static final Map<String, DayOfWeek> DAY_LOOKUP = Map.of(
        "MON", DayOfWeek.MONDAY,
        "TUE", DayOfWeek.TUESDAY,
        "WED", DayOfWeek.WEDNESDAY,
        "THU", DayOfWeek.THURSDAY,
        "FRI", DayOfWeek.FRIDAY,
        "SAT", DayOfWeek.SATURDAY,
        "SUN", DayOfWeek.SUNDAY
    );

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
        validateRequest(request);

        // TODO (Database): persist booking request in DB and return generated id.
        // Example flow:
        // 1) validate request data
        // 2) INSERT into appointment_request table
        // 3) optionally INSERT appointment/advisor assignment record
        // 4) return generated booking id from DB
        String shortToken = UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        return "BK-" + shortToken;
    }

    private void validateRequest(BookingRequest request) {
        if (request == null) {
            throw validationError("Booking request cannot be empty.");
        }

        String studentName = getRequiredString(request, "Student name is required.", "studentName", "name");
        if (!studentName.matches("^[A-Za-z ]+$")) {
            throw validationError("Student name must contain letters and spaces only (no numbers or special characters).");
        }

        String studentId = getRequiredString(request, "Student ID is required.", "studentId", "id");
        if (!studentId.matches("^\\d+$")) {
            throw validationError("Student ID must contain digits only (no letters or special characters).");
        }
        if (studentId.length() != 9) {
            throw validationError("Student ID must be exactly 9 digits.");
        }

        LocalDate preferredDate = getRequiredDate(request, "Preferred date is required.", "preferredDate", "date");
        if (preferredDate.isBefore(LocalDate.now())) {
            throw validationError("Preferred date must be today or a future date.");
        }
        if (!isSchoolDay(preferredDate)) {
            throw validationError("Preferred date must be on a school day (Monday to Friday).");
        }

        LocalTime preferredTime = getRequiredTime(request, "Preferred time is required.", "preferredTime", "time");
        String advisorName = getRequiredString(request, "Advisor name is required.", "advisorName", "advisor", "advisorProfileName");
        validateAdvisorOfficeHours(advisorName, preferredDate, preferredTime);
    }

    private void validateAdvisorOfficeHours(String advisorName, LocalDate preferredDate, LocalTime preferredTime) {
        String normalizedAdvisorName = normalizeAdvisorName(advisorName);
        List<AdvisorProfile> advisors = getAllAdvisors();
        AdvisorProfile advisor = advisors.stream()
            .filter(a -> a.getAdvisorName().equalsIgnoreCase(normalizedAdvisorName))
            .findFirst()
            .orElseGet(() -> advisors.stream()
                .filter(a -> normalizedAdvisorName.toLowerCase(Locale.ROOT).contains(a.getAdvisorName().toLowerCase(Locale.ROOT))
                    || a.getAdvisorName().toLowerCase(Locale.ROOT).contains(normalizedAdvisorName.toLowerCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> validationError("Selected advisor was not found. Please pick an advisor from the list.")));

        String officeHours = advisor.getOfficeHours();
        String[] parts = officeHours.split("\\s+", 2);
        if (parts.length < 2) {
            throw validationError("Advisor office hours are currently unavailable. Please choose another advisor or time.");
        }

        List<DayOfWeek> officeDays = Arrays.stream(parts[0].split("/"))
            .map(String::trim)
            .map(s -> DAY_LOOKUP.get(s.toUpperCase(Locale.ROOT)))
            .filter(d -> d != null)
            .toList();

        String[] timeRange = parts[1].trim().split("-");
        if (timeRange.length != 2) {
            throw validationError("Advisor office hour format is invalid. Please choose another advisor or time.");
        }

        LocalTime start;
        LocalTime end;
        try {
            start = LocalTime.parse(timeRange[0].trim());
            end = LocalTime.parse(timeRange[1].trim());
        } catch (DateTimeParseException ex) {
            throw validationError("Advisor office hour format is invalid. Please choose another advisor or time.");
        }

        if (!officeDays.contains(preferredDate.getDayOfWeek())) {
            String availableDays = officeDays.stream()
                .map(d -> d.name().substring(0, 1) + d.name().substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(", "));
            throw validationError("Preferred time is outside this advisor's office days. Available days: " + availableDays + ".");
        }

        if (preferredTime.isBefore(start) || preferredTime.isAfter(end)) {
            throw validationError("Preferred time must be within the advisor's office hours: " + officeHours + ".");
        }
    }

    private boolean isSchoolDay(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
    }

    private String normalizeAdvisorName(String advisorName) {
        if (advisorName == null) {
            return "";
        }
        return advisorName.split("\\|")[0].trim();
    }

    private String getRequiredString(BookingRequest request, String requiredMessage, String... propertyNames) {
        Object rawValue = getPropertyValue(request, propertyNames);
        if (rawValue == null) {
            throw validationError(requiredMessage);
        }

        String value = rawValue.toString().trim();
        if (value.isEmpty()) {
            throw validationError(requiredMessage);
        }
        return value;
    }

    private LocalDate getRequiredDate(BookingRequest request, String requiredMessage, String... propertyNames) {
        Object rawValue = getPropertyValue(request, propertyNames);
        if (rawValue == null) {
            throw validationError(requiredMessage);
        }
        if (rawValue instanceof LocalDate date) {
            return date;
        }

        String value = rawValue.toString().trim();
        if (value.isEmpty()) {
            throw validationError(requiredMessage);
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw validationError("Preferred date must use yyyy-MM-dd format.");
        }
    }

    private LocalTime getRequiredTime(BookingRequest request, String requiredMessage, String... propertyNames) {
        Object rawValue = getPropertyValue(request, propertyNames);
        if (rawValue == null) {
            throw validationError(requiredMessage);
        }
        if (rawValue instanceof LocalTime time) {
            return time;
        }

        String value = rawValue.toString().trim();
        if (value.isEmpty()) {
            throw validationError(requiredMessage);
        }

        try {
            return LocalTime.parse(value);
        } catch (DateTimeParseException ex) {
            throw validationError("Preferred time must use HH:mm format.");
        }
    }

    private Object getPropertyValue(BookingRequest request, String... propertyNames) {
        for (String property : propertyNames) {
            Object value = invokeAccessor(request, "get" + capitalize(property));
            if (value == null) {
                value = invokeAccessor(request, "is" + capitalize(property));
            }
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private Object invokeAccessor(BookingRequest request, String methodName) {
        try {
            Method method = request.getClass().getMethod(methodName);
            return method.invoke(request);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }

    private IllegalArgumentException validationError(String details) {
        return new IllegalArgumentException(details + " " + FORM_ERROR_FOOTER);
    }
}
