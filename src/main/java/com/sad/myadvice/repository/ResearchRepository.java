package com.sad.myadvice.repository;

import com.sad.myadvice.entity.ResearchAreas;
import com.sad.myadvice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// FIX: was findByUser(AdminUser) — now uses shared User entity
public interface ResearchRepository extends JpaRepository<ResearchAreas, Long> {
    List<ResearchAreas> findByUser(User user);
}