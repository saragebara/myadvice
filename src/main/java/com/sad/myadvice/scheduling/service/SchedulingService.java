package com.sad.myadvice.scheduling.service;

import com.sad.myadvice.entity.*;
import com.sad.myadvice.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchedulingService {
    //repository
    private final ScheduleRepository scheduleRepository;
    private final ScheduleItemRepository scheduleItemRepository;
    private final CourseRepository courseRepository;
    public SchedulingService(ScheduleRepository scheduleRepository, ScheduleItemRepository scheduleItemRepository,
                            CourseRepository courseRepository) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleItemRepository = scheduleItemRepository;
        this.courseRepository = courseRepository;
    }

    //get all schedules of a student
    public List<Schedule> getSchedulesForStudent(User student) {
        return scheduleRepository.findByStudent(student);
    }

    //save a new schedule from a list of section strings
    //example format  for a section:"COMP2800 A | Mon 09:30-10:50 | Dr. Ahmed | Erie Hall 210"
    //generically: "course_code section | day and time | instructor | location"
    public Schedule saveSchedule(User student, String scheduleName, String term, List<String> sections) {
        Schedule schedule = new Schedule();
        schedule.setStudent(student);
        schedule.setScheduleName(scheduleName);
        schedule.setTerm(term);
        schedule.setCreatedAt(LocalDateTime.now());
        scheduleRepository.save(schedule); //saving the schedule

        for (String section : sections) { //going through each section, parsing, and if there's an item then save it
            ScheduleItem item = parseSectionString(section, schedule);
            if (item != null) scheduleItemRepository.save(item);
        }
        return schedule; //return schedule 
    }

    //delete schedule
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId); //remove id
    }

    //get items for a schedule
    public List<ScheduleItem> getItemsForSchedule(Schedule schedule) {
        return scheduleItemRepository.findBySchedule(schedule);
    }

    //parse section string into a ScheduleItem
    //Format: "course_code section | day and time | instructor | location"
    private ScheduleItem parseSectionString(String section, Schedule schedule) {
        try {
            String[] parts = section.split("\\|");
            if (parts.length < 4) return null;

            String courseAndSection = parts[0].trim(); //ex "COMP2800 A"
            String dayTime = parts[1].trim(); //ex "Mon 09:30-10:50"
            String instructor = parts[2].trim(); //ex "Dr. Ahmed"
            String location = parts[3].trim(); //ex "Erie Hall 210"

            //extracting the course code + section code
            String[] codeparts = courseAndSection.split(" ");
            String courseCode = codeparts[0].trim(); //course code, "COMP2800"
            String sectionCode = codeparts.length > 1
                ? codeparts[1].trim() : "A"; //section code, "A"

            //looking up the course (both with and without hyphen)
            Course course = courseRepository.findByCode(courseCode);
            if (course == null) {
                //before returning null try adding hyphen, COMP2800 -> COMP-2800
                String hyphenated = courseCode.replaceAll("([A-Z]+)(\\d+)", "$1-$2");
                course = courseRepository.findByCode(hyphenated);
            }
            if (course == null) return null; //otherwise return null

            ScheduleItem item = new ScheduleItem();
            item.setSchedule(schedule);
            item.setCourse(course);
            item.setSectionCode(sectionCode);
            item.setInstructor(instructor);
            item.setDayTime(dayTime);
            item.setLocation(location);
            return item;
        } catch (Exception e) {
            return null;
        }
    }
}