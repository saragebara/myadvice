package com.sad.myadvice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sad.myadvice.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
