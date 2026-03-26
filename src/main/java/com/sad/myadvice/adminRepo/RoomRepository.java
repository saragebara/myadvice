package com.sad.myadvice.adminRepo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sad.myadvice.adminEntity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
