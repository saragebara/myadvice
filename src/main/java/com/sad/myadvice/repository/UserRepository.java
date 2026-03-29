package com.sad.myadvice.repository;

import com.sad.myadvice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for managing User entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByStudentId(String studentId);
    List<User> findByRole(User.Role role);

    /**
     * Retrieves users by multiple roles.
     * @param roles the list of roles to filter users by.
     * @return a list of users matching the given roles.
     */
    List<User> findByRoleIn(List<User.Role> roles);
}