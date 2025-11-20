package com.stayeasy.api.dto;

import java.math.BigDecimal;

// DTO นี้จะเหมือน Entity 'Room' แต่ไม่มีข้อมูล nhạy cảm
// และช่วยให้เราควบคุมข้อมูลที่จะส่งกลับไปได้
public record RoomResponse(
        Long id,
        String roomNumber,
        String roomType,
        BigDecimal pricePerNight,
        String description
) {
}