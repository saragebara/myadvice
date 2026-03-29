package com.sad.myadvice.repository;

import com.sad.myadvice.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for managing Course entities.
 */
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByYearLevel(int yearLevel);
    List<Course> findByIsRequired(boolean isRequired);
    Course findByCode(String code);

    /**
     * Retrieves courses by multiple year levels.
     * @param yearLevels the list of year levels to filter courses by.
     * @return a list of courses matching the given year levels.
     */
    List<Course> findByYearLevelIn(List<Integer> yearLevels);
}