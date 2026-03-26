package com.sad.myadvice.repository;

/* --------- BOOKINGS - APPOINTMENT SLOTS ---------  */

import com.sad.myadvice.entity.AvailabilitySlot;
import com.sad.myadvice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    //Get all slots for a faculty member
    List<AvailabilitySlot> findByFaculty(User faculty);

    //Get only recurring slots for a faculty member
    List<AvailabilitySlot> findByFacultyAndIsRecurring(User faculty, boolean isRecurring);
}