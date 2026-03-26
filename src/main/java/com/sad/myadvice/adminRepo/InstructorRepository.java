package com.sad.myadvice.adminRepo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sad.myadvice.adminEntity.Instructor;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
}