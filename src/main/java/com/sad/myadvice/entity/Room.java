package com.sad.myadvice.adminEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomNumber;
    private String building;

    // Getters and setters
    //id
    public Long getId() { return id; }
    //room number
    public String getRoomNumber() { return roomNumber;}
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    //building
    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }
}
