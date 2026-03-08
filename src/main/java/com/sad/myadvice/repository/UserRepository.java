package com.sad.myadvice.repository;

import com.sad.myadvice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByStudentId(String studentId);
    List<User> findByRole(User.Role role);
}