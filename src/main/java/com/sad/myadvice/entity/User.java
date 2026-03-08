package com.sad.myadvice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id //(Primary key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto-increments id every time new user is added
    private Long id;

    private String name; //user's full name
    private String email; //user's email

    @Enumerated(EnumType.STRING)
    private Role role;  //students, faculty, staff

    private String studentId;  //student id, NULL for faculty/staff

    public enum Role { STUDENT, FACULTY, STAFF }

    //----Getters and setters----
    //id
    public Long getId() { return id; }
    //name
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    //email
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    //role (student/faculty/staff)
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    //student id
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
}