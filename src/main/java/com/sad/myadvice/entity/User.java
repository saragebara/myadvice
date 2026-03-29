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

    private String password; //user's password for login

    public enum Role { STUDENT, FACULTY, STAFF, ADMIN }

    @Enumerated(EnumType.STRING)
    private Major major;  // null for faculty/staff

    //advising notes written by faculty, visible to the student
    @Column(columnDefinition = "TEXT")
    private String advisingNotes;

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
    //password
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    //major (for student)
    public Major getMajor() { return major; }
    public void setMajor(Major major) { this.major = major; }
    //advising notes
    public String getAdvisingNotes() { return advisingNotes; }
    public void setAdvisingNotes(String advisingNotes) { this.advisingNotes = advisingNotes; }
}