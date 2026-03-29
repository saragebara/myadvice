package com.sad.myadvice.reports.service;

import com.sad.myadvice.entity.Appointment;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.AppointmentRepository;
import com.sad.myadvice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReportsService {
    private static final Logger logger = LoggerFactory.getLogger(ReportsService.class);

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public ReportsService(UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    //Dashboard summary statistics -------------------------------------
    public long getTotalStudents() {
        return userRepository.findByRole(User.Role.STUDENT).size();
    }
    public long getTotalFaculty() {
        return userRepository.findByRole(User.Role.FACULTY).size();
    }
    public long getTotalAppointments() {
        return appointmentRepository.count();
    }
    public String getMostBookedFacultyName() {
        List<User> faculty = userRepository.findByRole(User.Role.FACULTY);
        return faculty.stream()
            .max(Comparator.comparingInt(f ->
                appointmentRepository.findByFaculty(f).size()))
            .map(User::getName)
            .orElse("N/A");
    }

    //Students report ----------------------------------------------------------
    public List<User> getAllStudents() {
        return userRepository.findByRole(User.Role.STUDENT);
    }

    public List<User> filterStudents(String studentId, String name, String major, String year) {
        return userRepository.findByRole(User.Role.STUDENT).stream()
            .filter(s -> studentId == null || studentId.isBlank()
                || (s.getStudentId() != null && s.getStudentId().contains(studentId.trim())))
            .filter(s -> name == null || name.isBlank()
                || s.getName().toLowerCase().contains(name.toLowerCase().trim()))
            .filter(s -> major == null || major.isBlank() || "All".equals(major)
                || (s.getMajor() != null && s.getMajor().getFullName().contains(major)))
            .toList();
    }

    //Faculty report -------------------------------------------------------------
    public List<User> getAllFaculty() {
        return userRepository.findByRole(User.Role.FACULTY);
    }

    public List<User> filterFaculty(String facultyId, String name) {
        return userRepository.findByRole(User.Role.FACULTY).stream()
            .filter(f -> facultyId == null || facultyId.isBlank()
                || (f.getStudentId() != null && f.getStudentId().contains(facultyId.trim())))
            .filter(f -> name == null || name.isBlank()
                || f.getName().toLowerCase().contains(name.toLowerCase().trim()))
            .toList();
    }

    public int getAppointmentCountForFaculty(User faculty) {
        return appointmentRepository.findByFaculty(faculty).size();
    }

    //Appointment analytics -------------------------------------------------------
    public List<Map.Entry<User, Integer>> getTopStudentsByAppointments(int limit) {
        return userRepository.findByRole(User.Role.STUDENT).stream()
            .map(s -> Map.entry(s, appointmentRepository.findByStudent(s).size()))
            .filter(e -> e.getValue() > 0)
            .sorted(Map.Entry.<User, Integer>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    public List<Map.Entry<User, Integer>> getTopFacultyByAppointments(int limit) {
        return userRepository.findByRole(User.Role.FACULTY).stream()
            .map(f -> Map.entry(f, appointmentRepository.findByFaculty(f).size()))
            .filter(e -> e.getValue() > 0)
            .sorted(Map.Entry.<User, Integer>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * Checks if a faculty member is associated with a specific student.
     * @param faculty the faculty member.
     * @param studentId the student ID.
     * @return true if the faculty is associated with the student, false otherwise.
     */
    public boolean isFacultyAssociatedWithStudent(User faculty, String studentId) {
        return appointmentRepository.findByFaculty(faculty).stream()
            .anyMatch(appointment -> appointment.getStudent().getStudentId().equals(studentId));
    }

    /**
     * Retrieves the report for a specific student.
     * @param studentId the student ID.
     * @return the student report.
     */
    public synchronized Map<String, Object> generateStudentReport(String studentId, User currentUser) {
        enforceAccess(currentUser, "student-report");
        User student = userRepository.findByStudentId(studentId);
        if (student == null || student.getRole() != User.Role.STUDENT) {
            throw new IllegalArgumentException("Invalid student ID or user is not a student.");
        }

        Map<String, Object> report = new HashMap<>();
        report.put("name", student.getName());
        report.put("email", student.getEmail());
        report.put("appointments", appointmentRepository.findByStudent(student));
        return report;
    }

    /**
     * Retrieves the report for a specific faculty member.
     * @param facultyId the ID of the faculty member.
     * @return the faculty report.
     */
    public synchronized Map<String, Object> generateFacultyReport(String facultyId, User currentUser) {
        enforceAccess(currentUser, "faculty-report");
        User faculty = userRepository.findByStudentId(facultyId);
        if (faculty == null || faculty.getRole() != User.Role.FACULTY) {
            throw new IllegalArgumentException("Invalid faculty ID or user is not a faculty member.");
        }

        Map<String, Object> report = new HashMap<>();
        report.put("name", faculty.getName());
        report.put("email", faculty.getEmail());
        report.put("appointments", appointmentRepository.findByFaculty(faculty));
        return report;
    }

    /**
     * Generates a system-wide report for staff/admin.
     * @return the admin report.
     */
    public synchronized Map<String, Object> generateAdminReport(User currentUser) {
        enforceAccess(currentUser, "admin-report");
        Map<String, Object> report = new HashMap<>();
        report.put("totalStudents", getTotalStudents());
        report.put("totalFaculty", getTotalFaculty());
        report.put("totalAppointments", getTotalAppointments());
        report.put("mostBookedFaculty", getMostBookedFacultyName());
        return report;
    }

    /**
     * Retrieves system-wide analytics.
     * @return analytics data.
     */
    public Object getAnalytics() {
        // Implement logic to fetch and return analytics data
        return null; // Placeholder
    }

    public boolean hasAccess(User user, String reportType) {
        switch (user.getRole()) {
            case STUDENT:
                if ("student-report".equals(reportType) || "own-report".equals(reportType)) {
                    return true;
                }
                break;
            case FACULTY:
                if ("faculty-report".equals(reportType)) {
                    return true;
                }
                break;
            case STAFF:
                if (!"admin-report".equals(reportType)) {
                    return true;
                }
                break;
            case ADMIN:
                return true; // Full access
        }
        logger.warn("Unauthorized access attempt by user: {} for report type: {}", user.getEmail(), reportType);
        return false;
    }

    public void enforceAccess(User user, String reportType) {
        if (!hasAccess(user, reportType)) {
            throw new SecurityException("Access denied for user: " + user.getEmail());
        }
    }

    public boolean validateRoleAccess(User user, String reportType) {
        switch (user.getRole()) {
            case STUDENT:
                return "student-report".equals(reportType) || "own-report".equals(reportType);
            case FACULTY:
                return "faculty-report".equals(reportType);
            case STAFF:
                return !"admin-report".equals(reportType);
            case ADMIN:
                return true;
            default:
                return false;
        }
    }

    public Object getStudentReport(String studentId) {
        // Implementation here
        return null;
    }
}

@RestController
@RequestMapping("/students")
public class ReportsService {

    @GetMapping("/{id}/export-transcript")
    public ResponseEntity<String> exportTranscript(@PathVariable Long id, User authenticatedUser) {
        if (id == null || authenticatedUser == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        // Ensure the authenticated user is the student
        if (!authenticatedUser.getId().equals(id) || authenticatedUser.getRole() != User.Role.STUDENT) {
            throw new SecurityException("Access denied");
        }

        // Fetch the transcript
        List<Transcript> transcripts = transcriptRepository.findByStudent(authenticatedUser);
        if (transcripts.isEmpty()) {
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transcript.csv")
                .body("Course Name,Course Code,Status,Grade\nNo records found\n");
        }

        // Generate CSV content
        StringWriter csvWriter = new StringWriter();
        csvWriter.append("Course Name,Course Code,Status,Grade\n");
        for (Transcript transcript : transcripts) {
            csvWriter.append(transcript.getCourse() != null ? transcript.getCourse().getName() : "N/A").append(",")
                .append(transcript.getCourse() != null ? transcript.getCourse().getCode() : "N/A").append(",")
                .append(transcript.getStatus() != null ? transcript.getStatus().name() : "N/A").append(",")
                .append(transcript.getGrade() != null ? transcript.getGrade().toString() : "N/A").append("\n");
        }

        // Return CSV as response
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "text/csv")
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transcript.csv")
            .body(csvWriter.toString());
    }

    public List<Transcript> fetchTranscripts(User student) {
        return transcriptRepository.findByStudent(student);
    }
}