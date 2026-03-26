package com.sad.myadvice.reports.service;

import com.sad.myadvice.entity.Appointment;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.AppointmentRepository;
import com.sad.myadvice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportsService {
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
}