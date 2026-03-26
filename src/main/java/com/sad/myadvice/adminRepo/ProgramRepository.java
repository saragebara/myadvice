package com.sad.myadvice.adminRepo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sad.myadvice.adminEntity.Program;

public interface ProgramRepository extends JpaRepository<Program, Long> {
}
