package com.stayeasy.api.model;

import jakarta.persistence.*;

import java.time.LocalDate; // ใช้ LocalDate สำหรับวันที่ (ไม่มีเวลา)
import java.util.Objects;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- ความสัมพันธ์ (Relationships) ---

    @ManyToOne(fetch = FetchType.LAZY) // (LAZY = โหลดเมื่อจำเป็น)
    @JoinColumn(name = "user_id", nullable = false) // ชื่อคอลัมน์ Foreign Key
    private User user; // ผู้ใช้ที่ทำการจอง

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false) // ชื่อคอลัมน์ Foreign Key
    private Room room; // ห้องที่ถูกจอง

    // --- ข้อมูลการจอง ---

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    // --- Constructors ---
    public Booking() {
        // Constructor ว่างสำหรับ JPA
    }

    public Booking(User user, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        this.user = user;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    // --- Getters and Setters ---
    // (IntelliJ สามารถ Generate ให้ได้: Alt+Insert -> Getter and Setter)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    // --- equals() and hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}