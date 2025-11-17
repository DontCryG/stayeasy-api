package com.stayeasy.api.dto;

// DTO สำหรับส่ง JWT Token กลับไปให้ Client หลังจาก Login/Register สำเร็จ
public record AuthResponse(
        String accessToken
) {
}