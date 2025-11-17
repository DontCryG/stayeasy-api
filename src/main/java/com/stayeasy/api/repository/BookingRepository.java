package com.stayeasy.api.repository;

import com.stayeasy.api.model.Booking;
import com.stayeasy.api.model.Room;
import com.stayeasy.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // ค้นหาการจองทั้งหมดของผู้ใช้คนใดคนหนึ่ง
    List<Booking> findByUser(User user);

    // --- นี่คือ Query ที่ซับซ้อนและสำคัญที่สุด ---
    // ค้นหาการจองที่มีวันที่ทับซ้อน (overlapping) สำหรับห้องที่กำหนด
    // เราจะใช้ query นี้เพื่อตรวจสอบว่าห้อง "ว่าง" หรือไม่
    @Query("SELECT b FROM Booking b WHERE b.room = :room AND (b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate)")
    List<Booking> findOverlappingBookings(
            @Param("room") Room room,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );
}