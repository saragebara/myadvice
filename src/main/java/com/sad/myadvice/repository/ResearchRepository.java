package com.sad.myadvice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sad.myadvice.entity.AdminUser;
import com.sad.myadvice.entity.ResearchAreas;

public interface ResearchRepository extends JpaRepository<ResearchAreas, Long> {
    List<ResearchAreas> findByUser(AdminUser user);
}
