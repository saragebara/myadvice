package com.sad.myadvice;

import com.sad.myadvice.entity.Instructor;
import com.sad.myadvice.entity.Room;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.entity.ResearchAreas;
import com.sad.myadvice.repository.InstructorRepository;
import com.sad.myadvice.repository.ResearchRepository;
import com.sad.myadvice.repository.RoomRepository;
import com.sad.myadvice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(5)
public class AdminDataSeeder implements CommandLineRunner {

    private final InstructorRepository instructorRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ResearchRepository researchRepository;

    public AdminDataSeeder(InstructorRepository instructorRepository,
                           RoomRepository roomRepository,
                           UserRepository userRepository,
                           ResearchRepository researchRepository) {
        this.instructorRepository = instructorRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.researchRepository = researchRepository;
    }

    @Override
    public void run(String... args) {
        seedInstructors();
        seedRooms();
        seedResearchAreas();
    }

    private void seedInstructors() {
        if (instructorRepository.count() > 0) return;

        // Link instructors to faculty users where possible
        List<User> faculty = userRepository.findByRole(User.Role.FACULTY);

        String[][] data = {
            {"Dr. Maniatis",  "maniatis@uwindsor.ca",  "Computer Science"},
            {"Dr. Ahmed",     "ahmed@uwindsor.ca",     "Computer Science"},
            {"Dr. Campbell",  "campbell@uwindsor.ca",  "Computer Science"},
            {"Dr. Taylor",    "taylor@uwindsor.ca",    "Computer Science"},
            {"Dr. Singh",     "singh@uwindsor.ca",     "Computer Science"},
            {"Dr. Morgan",    "morgan@uwindsor.ca",    "Computer Science"},
            {"Dr. Nguyen",    "nguyen@uwindsor.ca",    "Computer Science"},
            {"Prof. Lee",     "lee@uwindsor.ca",       "Mathematics"},
            {"Dr. Ibrahim",   "ibrahim@uwindsor.ca",   "Computer Science"},
            {"Dr. Kim",       "kim@uwindsor.ca",       "Computer Science"},
        };

        for (String[] row : data) {
            Instructor inst = new Instructor();
            inst.setName(row[0]);
            inst.setEmail(row[1]);
            inst.setDepartment(row[2]);

            // Link to User if email matches
            faculty.stream()
                .filter(u -> u.getEmail().equals(row[1]))
                .findFirst()
                .ifPresent(inst::setUser);

            instructorRepository.save(inst);
        }

        System.out.println("✓ Instructors seeded: " + instructorRepository.count());
    }

    private void seedRooms() {
        if (roomRepository.count() > 0) return;

        String[][] rooms = {
            {"210", "Erie Hall"},
            {"101", "Lambton Tower"},
            {"220", "Essex Hall"},
            {"120", "Erie Hall"},
            {"310", "Erie Hall"},
            {"205", "Lambton Tower"},
            {"110", "Essex Hall"},
            {"204", "Memorial Hall"},
            {"101", "Memorial Hall"},
            {"302", "Chrysler Hall North"},
        };

        for (String[] r : rooms) {
            Room room = new Room();
            room.setRoomNumber(r[0]);
            room.setBuilding(r[1]);
            roomRepository.save(room);
        }

        System.out.println("✓ Rooms seeded: " + roomRepository.count());
    }

    private void seedResearchAreas() {
        if (researchRepository.count() > 0) return;

        // Get the test faculty member
        User faculty = userRepository.findByEmail("maniatis@uwindsor.ca");
        if (faculty == null) {
            // Fall back to any faculty
            List<User> allFaculty = userRepository.findByRole(User.Role.FACULTY);
            if (allFaculty.isEmpty()) return;
            faculty = allFaculty.get(0);
        }

        String[][] areas = {
            {"Software Engineering",    "Research in software development methodologies and tools"},
            {"Database Systems",        "Advanced database design and query optimization"},
            {"Artificial Intelligence", "Machine learning and neural network applications"},
            {"Computer Networks",       "Network security and distributed systems"},
        };

        for (String[] area : areas) {
            ResearchAreas ra = new ResearchAreas();
            ra.setResearchArea(area[0]);
            ra.setDescription(area[1]);
            ra.setUser(faculty);
            researchRepository.save(ra);
        }

        System.out.println("✓ Research areas seeded");
    }
}