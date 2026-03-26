package com.sad.myadvice.adminEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "research_areas")
public class ResearchAreas {
    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto-increments id every time new user is added
    private Long id;

    private String researchArea; //research area of interest
    private String description; //description of research area

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AdminUser user;

    //getters and setters
    //id
    public Long getId() { return id; }
    //research area
    public String getResearchArea() { return researchArea; }
    public void setResearchArea(String researchArea) { this.researchArea = researchArea; }
    //description
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    //user    
    public AdminUser getUser() { return user; }
    public void setUser(AdminUser user) { this.user = user; }

}
