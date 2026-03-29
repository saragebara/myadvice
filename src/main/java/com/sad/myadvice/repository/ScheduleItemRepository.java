package com.sad.myadvice.repository;

import com.sad.myadvice.entity.ScheduleItem;
import com.sad.myadvice.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Long> {
    //filter based on schedule
    List<ScheduleItem> findBySchedule(Schedule schedule);
}