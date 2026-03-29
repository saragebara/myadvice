package com.sad.myadvice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "research_areas")
public class ResearchAreas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String researchArea;
    private String description;

    // FIX: was AdminUser — now uses the shared User entity (same users table)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() { return id; }
    public String getResearchArea() { return researchArea; }
    public void setResearchArea(String researchArea) { this.researchArea = researchArea; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}