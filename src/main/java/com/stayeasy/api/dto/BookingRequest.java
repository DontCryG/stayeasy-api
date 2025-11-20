package com.stayeasy.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BookingRequest(
        @NotNull(message = "Room ID cannot be null")
        Long roomId,

        @NotNull(message = "Check-in date cannot be null")
        @FutureOrPresent(message = "Check-in date must be today or in the future")
        LocalDate checkInDate,

        @NotNull(message = "Check-out date cannot be null")
        @Future(message = "Check-out date must be in the future")
        LocalDate checkOutDate
) {
    // (เราจะเพิ่ม Validation ที่ซับซ้อนขึ้น
    // เช่น checkOutDate > checkInDate ใน Service)
}