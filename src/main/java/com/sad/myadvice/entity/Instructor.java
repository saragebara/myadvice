package com.sad.myadvice.adminEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; //instructor's full name
    private String email; //instructor's email
    //---MAY GET RID OF DEP AREA IN FAVOR OF RESEARCH AREAS TABLE OR ALL TOGETHER
    private String department; //instructor's department

    //getters and setters
    //id
    public Long getId() { return id; }
    //name
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    //email
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    //department
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
