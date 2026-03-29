package com.sad.myadvice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sad.myadvice.entity.AdminCourse;
import com.sad.myadvice.entity.Instructor;
import com.sad.myadvice.entity.Program;
import com.sad.myadvice.entity.Room;
import com.sad.myadvice.entity.Section;
import com.sad.myadvice.repository.AdminCourseRepository;
import com.sad.myadvice.repository.AdminUserRepository;
import com.sad.myadvice.repository.InstructorRepository;
import com.sad.myadvice.repository.ProgramRepository;
import com.sad.myadvice.repository.ResearchRepository;
import com.sad.myadvice.repository.RoomRepository;
import com.sad.myadvice.repository.SectionRepository;

@Configuration
public class TestDataLoader {

    @Bean
    CommandLineRunner initData(
            ProgramRepository programRepo,
            AdminCourseRepository courseRepo,
            InstructorRepository instructorRepo,
            RoomRepository roomRepo,
            ResearchRepository researchRepo,
            AdminUserRepository userRepo,
            SectionRepository sectionRepo
    ) {

        return args -> {

            // Create Program
            Program program = new Program();
            program.setName("ppp993pppp");
            programRepo.save(program);

            // Create Course
            AdminCourse course = new AdminCourse();
            course.setCourseCode("CMP399-92");
            course.setCourseName("tructr3es of 99a Computr");
            course.setProgram(program);
            courseRepo.save(course);

            // Create Instructor
            Instructor instructor = new Instructor();
            instructor.setName("Jhn Smt3hi99ngon");
            instructor.setEmail("ithng3t99on@schol.ca");
            instructor.setDepartment("Copu3r 99Science");
            instructorRepo.save(instructor);

            // Create Room
            Room room = new Room();
            room.setBuilding("Prs39t");
            room.setRoomNumber("993");
            roomRepo.save(room);

            // Create Section
            Section section = new Section();
            section.setSectionNumber("939");
            section.setDay("o9a3y");
            section.setTime("0 3PM");

            section.setCourse(course);
            section.setInstructor(instructor);
            section.setRoom(room);

            sectionRepo.save(section);

            System.out.println("✅ Test data inserted!");
        };
    }
}