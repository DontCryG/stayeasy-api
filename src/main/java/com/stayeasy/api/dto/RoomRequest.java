package com.stayeasy.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

// ใช้ record (Java 17+)
public record RoomRequest(
        @NotBlank(message = "Room number cannot be blank")
        String roomNumber,

        @NotBlank(message = "Room type cannot be blank")
        String roomType,

        @NotNull(message = "Price per night cannot be null")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal pricePerNight,

        String description // (Optional)
) {
}