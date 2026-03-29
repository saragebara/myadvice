package com.sad.myadvice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sad.myadvice.entity.Instructor;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
}