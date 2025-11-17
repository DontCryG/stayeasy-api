package com.stayeasy.api.model;

import jakarta.persistence.*;

import java.math.BigDecimal; // ใช้ BigDecimal สำหรับการเงิน
import java.util.Objects;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // เลขห้องควรจะไม่ซ้ำกัน
    private String roomNumber;

    @Column(nullable = false)
    private String roomType; // เช่น "Single", "Double", "Suite"

    @Column(nullable = false)
    private BigDecimal pricePerNight; // ราคาต่อคืน

    private String description; // คำอธิบาย (ไม่บังคับ)

    // --- Constructors ---
    public Room() {
        // Constructor ว่างสำหรับ JPA
    }

    public Room(String roomNumber, String roomType, BigDecimal pricePerNight, String description) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.description = description;
    }

    // --- Getters and Setters ---
    // (IntelliJ สามารถ Generate ให้ได้: Alt+Insert -> Getter and Setter)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // --- equals() and hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id) && Objects.equals(roomNumber, room.roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roomNumber);
    }
}