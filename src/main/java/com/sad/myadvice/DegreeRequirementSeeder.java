package com.sad.myadvice;

import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.CourseProgram;
import com.sad.myadvice.entity.Major;
import com.sad.myadvice.repository.CourseProgramRepository;
import com.sad.myadvice.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.HashMap;

/* DATA SEEDER FOR
DEGREE REQUIREMENTS FOR EACH PROGRAM
PROGRAMS INCLUDED: CSG, CSH, CSHAC, CIS, CSSE
 */

@Component
@Order(3) //Runs after CourseDataSeeder.java
public class DegreeRequirementSeeder implements CommandLineRunner {
    private final CourseRepository courseRepository;
    private final CourseProgramRepository courseProgramRepository;

    public DegreeRequirementSeeder(CourseRepository courseRepository, CourseProgramRepository courseProgramRepository) {
        this.courseRepository = courseRepository;
        this.courseProgramRepository = courseProgramRepository;
    }

    @Override
    public void run(String... args) {
        if (courseProgramRepository.count() > 0) return;

        seedProgram(Major.CSG, csgRequirements());
        seedProgram(Major.CSH, cshRequirements());
        seedProgram(Major.CSHAC, cshacRequirements());
        seedProgram(Major.CIS, cisRequirements());
        seedProgram(Major.CSSE, csseRequirements());

        System.out.println("YAY Degree requirements seeded successfully");
    }

    private void seedProgram(Major major, Map<String, Integer> requirements) {
        for (Map.Entry<String, Integer> entry : requirements.entrySet()) {
            Course course = courseRepository.findByCode(entry.getKey());
            //skip (continue) if null
            if (course == null) continue;
            //skip (continue) if it already exists
            if (courseProgramRepository.existsByCourseAndMajor(course, major)) continue;

            //create new course program with course, major, and recommend year
            CourseProgram cp = new CourseProgram();
            cp.setCourse(course);
            cp.setMajor(major);
            cp.setRecommendedYear(entry.getValue());
            courseProgramRepository.save(cp);
        }
    }

    // CSG - Bachelor of Computer Science (General) ----------------------------------
    private Map<String, Integer> csgRequirements() {
        Map<String, Integer> m = new HashMap<>();
        // Year 1
        m.put("COMP-1000", 1);
        m.put("COMP-1400", 1);
        m.put("COMP-1410", 1);
        m.put("MATH-1250", 1);
        m.put("MATH-1720", 1);
        // Year 2
        m.put("COMP-2120", 2);
        m.put("COMP-2540", 2);
        m.put("COMP-2560", 2);
        m.put("COMP-2650", 2);
        m.put("STAT-2910", 2);
        // Year 3
        m.put("COMP-3057", 3);
        m.put("COMP-3150", 3);
        m.put("COMP-3220", 3);
        m.put("COMP-3300", 3);
        m.put("COMP-3340", 3);
        return m;
    }

    // CSH - Bachelor of Computer Science (Honours) ----------------------------------
    private Map<String, Integer> cshRequirements() {
        Map<String, Integer> m = new HashMap<>();
        // Year 1
        m.put("COMP-1000", 1);
        m.put("COMP-1400", 1);
        m.put("COMP-1410", 1);
        m.put("MATH-1020", 1);
        m.put("MATH-1250", 1);
        m.put("MATH-1720", 1);
        m.put("MATH-1730", 1);
        // Year 2
        m.put("COMP-2120", 2);
        m.put("COMP-2140", 2);
        m.put("COMP-2310", 2);
        m.put("COMP-2540", 2);
        m.put("COMP-2560", 2);
        m.put("COMP-2650", 2);
        m.put("STAT-2910", 2);
        // Year 3
        m.put("COMP-3057", 3);
        m.put("COMP-3110", 3);
        m.put("COMP-3150", 3);
        m.put("COMP-3220", 3);
        m.put("COMP-3300", 3);
        m.put("COMP-3540", 3);
        m.put("COMP-3670", 3);
        m.put("MATH-3940", 3);
        // Year 4
        m.put("COMP-4400", 4);
        m.put("COMP-4540", 4);
        m.put("COMP-4990", 4);
        return m;
    }

    // CSHAC - Bachelor of CS (Honours Applied Computing) ----------------------------------
    private Map<String, Integer> cshacRequirements() {
        Map<String, Integer> m = new HashMap<>();
        // Year 1
        m.put("COMP-1000", 1);
        m.put("COMP-1400", 1);
        m.put("COMP-1410", 1);
        m.put("MATH-1250", 1);
        m.put("MATH-1720", 1);
        // Year 2
        m.put("COMP-2120", 2);
        m.put("COMP-2540", 2);
        m.put("COMP-2560", 2);
        m.put("COMP-2650", 2);
        m.put("STAT-2910", 2);
        // Year 3
        m.put("COMP-3057", 3);
        m.put("COMP-3150", 3);
        m.put("COMP-3220", 3);
        m.put("COMP-3300", 3);
        m.put("COMP-3340", 3);
        m.put("COMP-3400", 3);
        m.put("COMP-3670", 3);
        // Year 4
        m.put("COMP-4150", 4);
        m.put("COMP-4200", 4);
        m.put("COMP-4220", 4);
        m.put("COMP-4250", 4);
        m.put("COMP-4990", 4);
        return m;
    }

    // CIS - Bachelor of Science (Honours Computer Information Systems) ---------
    private Map<String, Integer> cisRequirements() {
        Map<String, Integer> m = new HashMap<>();
        // Year 1
        m.put("COMP-1000", 1);
        m.put("COMP-1400", 1);
        m.put("COMP-1410", 1);
        m.put("MATH-1250", 1);
        m.put("MATH-1720", 1);
        // Year 2
        m.put("COMP-2120", 2);
        m.put("COMP-2540", 2);
        m.put("COMP-2560", 2);
        m.put("COMP-2650", 2);
        m.put("STAT-2910", 2);
        // Year 3
        m.put("COMP-3057", 3);
        m.put("COMP-3150", 3);
        m.put("COMP-3220", 3);
        m.put("COMP-3300", 3);
        m.put("COMP-3340", 3);
        m.put("COMP-3400", 3);
        // Year 4
        m.put("COMP-4990", 4);
        return m;
    }

    // CSSE - Bachelor of CS (Honours with Software Engineering) ---------------
    private Map<String, Integer> csseRequirements() {
        Map<String, Integer> m = new HashMap<>();
        // Year 1
        m.put("COMP-1000", 1);
        m.put("COMP-1400", 1);
        m.put("COMP-1410", 1);
        m.put("MATH-1020", 1);
        m.put("MATH-1250", 1);
        m.put("MATH-1720", 1);
        m.put("MATH-1730", 1);
        // Year 2
        m.put("COMP-2120", 2);
        m.put("COMP-2140", 2);
        m.put("COMP-2310", 2);
        m.put("COMP-2540", 2);
        m.put("COMP-2560", 2);
        m.put("COMP-2650", 2);
        m.put("COMP-2800", 2);
        m.put("STAT-2910", 2);
        // Year 3
        m.put("COMP-3057", 3);
        m.put("COMP-3110", 3);
        m.put("COMP-3150", 3);
        m.put("COMP-3220", 3);
        m.put("COMP-3300", 3);
        m.put("COMP-3540", 3);
        m.put("COMP-3670", 3);
        // Year 4
        m.put("COMP-4110", 4);
        m.put("COMP-4400", 4);
        m.put("COMP-4540", 4);
        m.put("COMP-4800", 4);
        m.put("COMP-4990", 4);
        return m;
    }
}