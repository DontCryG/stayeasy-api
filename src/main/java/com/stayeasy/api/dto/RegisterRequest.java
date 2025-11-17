package com.stayeasy.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank; // ใช้ @NotBlank สำหรับ String
import jakarta.validation.constraints.Size;

// เราใช้ "record" (Java 17+) เพื่อสร้าง DTO ที่กระชับ
// มันจะสร้าง constructor, getters, equals, hashCode, toString ให้อัตโนมัติ
public record RegisterRequest(

        @NotBlank(message = "Email cannot be blank") // ห้ามว่าง
        @Email(message = "Invalid email format") // ต้องเป็นรูปแบบอีเมล
        String email, // เราใช้ email เป็น username

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, message = "Password must be at least 6 characters long") // รหัสผ่านต้องยาวอย่างน้อย 6 ตัว
        String password,

        @NotBlank(message = "Full name cannot be blank")
        String fullName
) {
}