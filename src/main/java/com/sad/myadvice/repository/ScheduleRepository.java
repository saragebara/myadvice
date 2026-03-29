package com.sad.myadvice.repository;

import com.sad.myadvice.entity.Schedule;
import com.sad.myadvice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    //filter list by students
    List<Schedule> findByStudent(User student);
    //filter list by students + the term
    List<Schedule> findByStudentAndTerm(User student, String term);
}