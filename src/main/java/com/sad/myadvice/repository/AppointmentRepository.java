package com.sad.myadvice.repository;

/* --------- BOOKINGS - APPOINTMENTS ---------  */

import com.sad.myadvice.entity.Appointment;
import com.sad.myadvice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for managing Appointment entities.
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Retrieves all appointments for a specific student.
     * @param student the student whose appointments are to be retrieved.
     * @return a list of appointments for the given student.
     */
    List<Appointment> findByStudent(User student);

    /**
     * Retrieves all appointments for a specific faculty member.
     * @param faculty the faculty member whose appointments are to be retrieved.
     * @return a list of appointments for the given faculty member.
     */
    List<Appointment> findByFaculty(User faculty);

    /**
     * Retrieves appointments for a specific student filtered by status.
     * @param student the student whose appointments are to be retrieved.
     * @param status the status to filter appointments by.
     * @return a list of appointments matching the criteria.
     */
    List<Appointment> findByStudentAndStatus(User student, Appointment.Status status);

    /**
     * Retrieves appointments for a specific faculty member filtered by status.
     * @param faculty the faculty member whose appointments are to be retrieved.
     * @param status the status to filter appointments by.
     * @return a list of appointments matching the criteria.
     */
    List<Appointment> findByFacultyAndStatus(User faculty, Appointment.Status status);

    /**
     * Retrieves all appointments across all faculty members, ordered by date and time in descending order.
     * @return a list of all appointments ordered by date and time.
     */
    List<Appointment> findAllByOrderByDateTimeDesc();
}