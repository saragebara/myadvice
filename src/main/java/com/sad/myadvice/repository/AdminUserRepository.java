package com.sad.myadvice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sad.myadvice.entity.AdminUser;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
}
