package com.sad.myadvice.booking.service;

import com.sad.myadvice.entity.AvailabilitySlot;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.AvailabilitySlotRepository;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AvailabilityService {

    private final AvailabilitySlotRepository slotRepository;

    public AvailabilityService(AvailabilitySlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    //get all slots for a faculty member
    public List<AvailabilitySlot> getSlotsForFaculty(User faculty) {
        return slotRepository.findByFaculty(faculty);
    }

    //add recurring weekly slot
    public AvailabilitySlot addRecurringSlot(User faculty, DayOfWeek day, LocalTime start, LocalTime end, int maxCapacity) {
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setFaculty(faculty);
        slot.setDayOfWeek(day);
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setRecurring(true);
        slot.setSpecificDate(null);
        slot.setMaxCapacity(maxCapacity);
        return slotRepository.save(slot);
    }

    //add a singular slot for specific date
    public AvailabilitySlot addOneOffSlot(User faculty, LocalDate date, LocalTime start, LocalTime end, int maxCapacity) {
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setFaculty(faculty);
        slot.setDayOfWeek(date.getDayOfWeek());
        slot.setSpecificDate(date);
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setRecurring(false);
        slot.setMaxCapacity(maxCapacity);
        return slotRepository.save(slot);
    }

    //remove a slot
    public void removeSlot(Long slotId) {
        slotRepository.deleteById(slotId);
    }

    //get available slots for a faculty on a specific date
    public List<AvailabilitySlot> getAvailableSlotsForDate(User faculty, LocalDate date) {
        List<AvailabilitySlot> allSlots = slotRepository.findByFaculty(faculty);

        return allSlots.stream()
            .filter(slot -> {
                if (slot.isRecurring()) {
                    //recurring slot matches if day of week matches
                    return slot.getDayOfWeek() == date.getDayOfWeek();
                } else {
                    //single slot matches if specific date matches
                    return date.equals(slot.getSpecificDate());
                }
            })
            .toList();
    }
}