package com.sad.myadvice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sad.myadvice.entity.Program;

public interface ProgramRepository extends JpaRepository<Program, Long> {
}
