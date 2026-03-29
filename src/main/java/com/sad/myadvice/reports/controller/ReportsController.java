package com.sad.myadvice.reports.controller;

import com.sad.myadvice.entity.User;
import com.sad.myadvice.reports.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class ReportsController {

    private final ReportsService reportsService;

    @Autowired
    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentReport(@PathVariable String studentId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        // Students can only access their own reports
        if (currentUser.getRole() == User.Role.STUDENT && !currentUser.getStudentId().equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        // Faculty can only access reports of their associated students
        if (currentUser.getRole() == User.Role.FACULTY && !reportsService.isFacultyAssociatedWithStudent(currentUser, studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        // Staff/Admin have full access
        if (currentUser.getRole() == User.Role.STAFF || currentUser.getRole() == User.Role.ADMIN) {
            return ResponseEntity.ok(reportsService.getStudentReport(studentId));
        }

        return ResponseEntity.ok(reportsService.getStudentReport(studentId));
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        // Only Staff/Admin can access analytics
        if (currentUser.getRole() != User.Role.STAFF && currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        return ResponseEntity.ok(reportsService.getAnalytics());
    }
}