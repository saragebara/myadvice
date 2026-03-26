package com.sad.myadvice.adminRepo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sad.myadvice.adminEntity.AdminUser;
import com.sad.myadvice.adminEntity.ResearchAreas;

public interface ResearchRepository extends JpaRepository<ResearchAreas, Long> {
    List<ResearchAreas> findByUser(AdminUser user);
}
