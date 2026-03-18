package com.myadvice.myadvice.config;

import com.myadvice.myadvice.entity.Student;
import com.myadvice.myadvice.repository.StudentRepository;
import com.myadvice.myadvice.service.StudentBackupService;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StudentBackupBootstrap implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final StudentBackupService studentBackupService;

    public StudentBackupBootstrap(StudentRepository studentRepository, StudentBackupService studentBackupService) {
        this.studentRepository = studentRepository;
        this.studentBackupService = studentBackupService;
    }

    @Override
    public void run(String... args) {
        if (studentRepository.count() > 0) {
            studentBackupService.backupStudents(studentRepository.findAll());
            return;
        }

        List<Student> backupStudents = studentBackupService.loadStudentsFromJson();
        if (backupStudents.isEmpty()) {
            return;
        }

        List<Student> restoredStudents = backupStudents.stream()
                .map(this::toNewStudent)
                .toList();

        studentRepository.saveAll(restoredStudents);
        studentBackupService.backupStudents(studentRepository.findAll());
    }

    private Student toNewStudent(Student source) {
        Student student = new Student();
        student.setFirstName(source.getFirstName());
        student.setLastName(source.getLastName());
        student.setEmail(source.getEmail());
        student.setCourse(source.getCourse());
        return student;
    }
}
