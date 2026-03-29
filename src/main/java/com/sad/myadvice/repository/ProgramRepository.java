package com.sad.myadvice.repository;

import com.sad.myadvice.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramRepository extends JpaRepository<Program, Long> {
}