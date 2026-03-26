package com.sad.myadvice.adminRepo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sad.myadvice.adminEntity.AdminUser;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
}
