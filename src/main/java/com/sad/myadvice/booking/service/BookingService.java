package com.sad.myadvice.booking.service;

import com.sad.myadvice.entity.Appointment;
import com.sad.myadvice.entity.AvailabilitySlot;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.AppointmentRepository;
import com.sad.myadvice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final AvailabilityService availabilityService;

    public BookingService(AppointmentRepository appointmentRepository,
                          UserRepository userRepository,
                          AvailabilityService availabilityService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.availabilityService = availabilityService;
    }

    //student booking an appointment
    @Transactional
    public Appointment bookAppointment(User student, User faculty, LocalDateTime dateTime, Appointment.ReasonType reason, String note) {
        if (student == null || faculty == null || dateTime == null || reason == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        // Validate against faculty availability
        boolean isAvailable = availabilityService.getAvailableSlotsForDate(faculty, dateTime.toLocalDate()).stream()
            .anyMatch(slot -> slot.getStartTime().equals(dateTime.toLocalTime()));

        if (!isAvailable) {
            throw new IllegalArgumentException("Selected time is not available");
        }

        // Validate against faculty schedule
        boolean isConflict = appointmentRepository.findByFaculty(faculty).stream()
            .anyMatch(appt -> appt.getDateTime().equals(dateTime) && appt.getStatus() == Appointment.Status.CONFIRMED);

        if (isConflict) {
            throw new IllegalArgumentException("Selected time is not available");
        }

        // Proceed with booking
        synchronized (faculty) {
            Appointment appt = new Appointment();
            appt.setStudent(student);
            appt.setFaculty(faculty);
            appt.setDateTime(dateTime);
            appt.setReasonType(reason);
            appt.setNote(note);
            appt.setStatus(Appointment.Status.PENDING);
            return appointmentRepository.save(appt);
        }
    }

    //get all appointments for a student
    public List<Appointment> getStudentAppointments(User student) {
        return appointmentRepository.findByStudent(student);
    }

    //get upcoming confirmed appointments for a student
    public List<Appointment> getUpcomingForStudent(User student) {
        return appointmentRepository
            .findByStudentAndStatus(student, Appointment.Status.CONFIRMED)
            .stream()
            .filter(a -> a.getDateTime().isAfter(LocalDateTime.now()))
            .toList();
    }

    //get pending requests for a faculty member
    public List<Appointment> getPendingForFaculty(User faculty) {
        return appointmentRepository
            .findByFacultyAndStatus(faculty, Appointment.Status.PENDING);
    }

    //get upcoming confirmed appointments for a faculty member
    public List<Appointment> getUpcomingForFaculty(User faculty) {
        return appointmentRepository
            .findByFacultyAndStatus(faculty, Appointment.Status.CONFIRMED)
            .stream()
            .filter(a -> a.getDateTime().isAfter(LocalDateTime.now()))
            .toList();
    }

    //faculty confirms an appointment
    public Appointment confirmAppointment(Long appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appt.setStatus(Appointment.Status.CONFIRMED);
        return appointmentRepository.save(appt);
    }

    //faculty rejects an appointment
    public Appointment rejectAppointment(Long appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appt.setStatus(Appointment.Status.REJECTED);
        return appointmentRepository.save(appt);
    }

    //student or faculty cancels an appointment
    public Appointment cancelAppointment(Long appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appt.setStatus(Appointment.Status.CANCELLED);
        return appointmentRepository.save(appt);
    }

    //get all faculty members (for student to browse)
    public List<User> getAllFaculty() {
        return userRepository.findByRole(User.Role.FACULTY);
    }

    //staff - get all appointments across all faculty
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAllByOrderByDateTimeDesc();
    }
}