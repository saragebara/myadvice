package com.sad.myadvice.repository;

/* --------- BOOKINGS - APPOINTMENTS ---------  */

import com.sad.myadvice.entity.Appointment;
import com.sad.myadvice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    //Get all appointments for a student
    List<Appointment> findByStudent(User student);

    //Get all appointments for a faculty member
    List<Appointment> findByFaculty(User faculty);

    //Get appointments by status for a student (e.g. all PENDING)
    List<Appointment> findByStudentAndStatus(User student, Appointment.Status status);

    //Get appointments by status for a faculty (e.g. all CONFIRMED)
    List<Appointment> findByFacultyAndStatus(User faculty, Appointment.Status status);

    //Get all appointments across all faculty (for staff oversight)
    List<Appointment> findAllByOrderByDateTimeDesc();
}