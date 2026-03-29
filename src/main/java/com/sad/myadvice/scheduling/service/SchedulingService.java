package com.sad.myadvice.scheduling.service;

import com.sad.myadvice.entity.*;
import com.sad.myadvice.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchedulingService {
    //repository
    private final ScheduleRepository scheduleRepository;
    private final ScheduleItemRepository scheduleItemRepository;
    private final CourseRepository courseRepository;
    private final TranscriptRepository transcriptRepository;

    public SchedulingService(ScheduleRepository scheduleRepository, ScheduleItemRepository scheduleItemRepository,
                            CourseRepository courseRepository, TranscriptRepository transcriptRepository) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleItemRepository = scheduleItemRepository;
        this.courseRepository = courseRepository;
        this.transcriptRepository = transcriptRepository;
    }

    //get all schedules of a student
    public List<Schedule> getSchedulesForStudent(User student) {
        return scheduleRepository.findByStudent(student);
    }

    @Transactional
    //save a new schedule from a list of section strings
    //example format  for a section:"COMP2800 A | Mon 09:30-10:50 | Dr. Ahmed | Erie Hall 210"
    //generically: "course_code section | day and time | instructor | location"
    public Schedule saveSchedule(User student, String scheduleName, String term, List<String> sections) {
        if (student == null || scheduleName == null || term == null || sections == null || sections.isEmpty()) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        if (getInProgressCourseCount(student) >= 5) {
            throw new IllegalArgumentException("Maximum course limit reached (5). Drop an existing course to add a new one.");
        }

        Schedule schedule = new Schedule();
        schedule.setStudent(student);
        schedule.setScheduleName(scheduleName);
        schedule.setTerm(term);
        schedule.setCreatedAt(LocalDateTime.now());
        scheduleRepository.save(schedule); //saving the schedule

        for (String section : sections) { //going through each section, parsing, and if there's an item then save it
            ScheduleItem item = parseSectionString(section, schedule);
            if (item != null) {
                validateCourseBeforeAdding(student, item.getCourse());
                scheduleItemRepository.save(item);
                try {
                    updateTranscriptWithCourse(student, item.getCourse());
                } catch (DataIntegrityViolationException e) {
                    throw new IllegalStateException("Duplicate course entry detected for transcript", e);
                }
            }
        }
        return schedule; //return schedule 
    }

    private void validateCourseBeforeAdding(User student, Course course) {
        Transcript existingTranscript = transcriptRepository.findByStudentAndCourse(student, course);
        if (existingTranscript != null) {
            if (existingTranscript.getStatus() == Transcript.Status.IN_PROGRESS) {
                throw new IllegalArgumentException("Course already in progress");
            } else if (existingTranscript.getStatus() == Transcript.Status.COMPLETED) {
                throw new IllegalArgumentException("Course already completed");
            }
        }
    }

    private long getInProgressCourseCount(User student) {
        return transcriptRepository.findByStudent(student).stream()
            .filter(t -> t.getStatus() == Transcript.Status.IN_PROGRESS)
            .count();
    }

    private void updateTranscriptWithCourse(User student, Course course) {
        boolean exists = transcriptRepository.findByStudentAndCourse(student, course) != null;
        if (!exists) {
            Transcript transcript = new Transcript();
            transcript.setStudent(student);
            transcript.setCourse(course);
            transcript.setStatus(Transcript.Status.IN_PROGRESS);
            transcriptRepository.save(transcript);
        }
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

    public List<Transcript> fetchTranscripts(User student) {
        return transcriptRepository.findByStudent(student);
    }
}

@RestController
@RequestMapping("/scheduling")
public class SchedulingService {

    @Transactional
    @DeleteMapping("/drop-course/{courseId}")
    public void dropCourse(@PathVariable Long courseId, @RequestParam Long studentId) {
        User student = userRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Student not found"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Transcript transcript = transcriptRepository.findByStudentAndCourse(student, course);
        if (transcript == null || transcript.getStatus() != Transcript.Status.IN_PROGRESS) {
            throw new IllegalArgumentException("Course is not in progress or does not exist in the transcript");
        }

        // Update transcript status to DROPPED
        transcript.setStatus(Transcript.Status.DROPPED);
        transcriptRepository.save(transcript);

        // Remove from schedule
        ScheduleItem scheduleItem = scheduleItemRepository.findByScheduleAndCourse(student.getSchedule(), course);
        if (scheduleItem != null) {
            scheduleItemRepository.delete(scheduleItem);
        }
    }
}