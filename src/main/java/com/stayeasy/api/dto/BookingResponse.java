package com.stayeasy.api.dto;

import java.time.LocalDate;

// DTO นี้จะแสดงข้อมูลสรุปของการจอง
// เราจะ "ซ้อน" RoomResponse DTO ที่เรามีอยู่แล้วเข้ามา
public record BookingResponse(
        Long bookingId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        RoomResponse room // ข้อมูลของห้องที่จอง
        // (เราไม่จำเป็นต้องส่งข้อมูล User กลับไป)
) {
}