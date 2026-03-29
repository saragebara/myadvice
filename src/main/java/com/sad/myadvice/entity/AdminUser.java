package com.sad.myadvice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AdminUser {
    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto-increments id every time new user is added
    private Long id;

    private String name; //user's full name
    private String email; //user's email

    @Enumerated(EnumType.STRING)
    private Role role;  //students, faculty, staff

    public enum Role {FACULTY, STAFF }

    //getters and setters
    //id
    public Long getId() { return id; }
    //name
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    //email
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    //role (faculty/staff)
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
