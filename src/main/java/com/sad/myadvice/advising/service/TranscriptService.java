package com.sad.myadvice.advising.service;

import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.Transcript;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.TranscriptRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service //tells Spring that this is a service class to manage automatically
public class TranscriptService {
    private final TranscriptRepository transcriptRepository;

    public TranscriptService(TranscriptRepository transcriptRepository) {
        this.transcriptRepository = transcriptRepository;
    }

    //Get all of a student's transcripts
    public List<Transcript> getFullTranscript(User student) {
        return transcriptRepository.findByStudent(student);
    }

    //Get only the completed courses for a student
    public List<Course> getCompletedCourses(User student) {
        // Used by advising/ui/screens/SchedullingScreen.java to pre-check
        // what courses are already completed before building a schedule.
        //findByStudentAndStatus is from TranscriptRepository. SQL query to find courses in a student's transcript that are completed
        //map(Transcript::getCourse) returns a clean list of courses instead of transcripts
        return transcriptRepository.findByStudentAndStatus(student, Transcript.Status.COMPLETED).stream()
            .map(Transcript::getCourse).toList();
    }

    //Get only the in-progress courses for a student
    public List<Course> getInProgressCourses(User student) {
        // Used by advising/ui/screens/SchedullingScreen.java to avoid
        // duplicate planning of courses the student is already taking.
        return transcriptRepository
        //Queries for courses that are in progress and returns a list of those courses
            .findByStudentAndStatus(student, Transcript.Status.IN_PROGRESS).stream()
            .map(t -> t.getCourse()).toList();
    }

    //Checks if a student has completed a course
    public boolean hasCompleted(User student, Course course) {
        Transcript t = transcriptRepository.findByStudentAndCourse(student, course);
        //checks if the course in the transcript isn't null and if its status is set as COMPLETED
        //returns true or false
        return t!=null && t.getStatus()==Transcript.Status.COMPLETED;
    }

    //Check if a student has completed OR is currently taking a course (for corequisite checks)
    public boolean hasCompletedOrInProgress(User student, Course course) {
        Transcript t = transcriptRepository.findByStudentAndCourse(student, course);
        return t!=null && (t.getStatus() == Transcript.Status.COMPLETED || t.getStatus() == Transcript.Status.IN_PROGRESS);
    }

    //Calculate the overall degree completion percentage
    public double getCompletionPercentage(User student, int totalRequiredCourses) {
        //gets all completed courses that are required for the student's major and calculates percentage
        long completed = transcriptRepository
            .findByStudentAndStatus(student, Transcript.Status.COMPLETED)
            .stream()
            .filter(t -> t.getCourse().isRequired())
            .count();
        //if total required courses is 0 then have a ternary just in case. otherwise division by 0 is an issue
        return totalRequiredCourses == 0 ? 0 : ((double) completed / totalRequiredCourses)*100;
    }
}