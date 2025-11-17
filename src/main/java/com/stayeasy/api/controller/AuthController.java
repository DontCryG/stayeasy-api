package com.stayeasy.api.controller;

import com.stayeasy.api.dto.AuthResponse;
import com.stayeasy.api.dto.LoginRequest;
import com.stayeasy.api.dto.RegisterRequest;
import com.stayeasy.api.service.AuthService;
import jakarta.validation.Valid; // Import นี้สำคัญ
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // บอก Spring ว่านี่คือ Controller ที่คืนค่าเป็น JSON
@RequestMapping("/api/auth") // กำหนด URL หลักสำหรับ Controller นี้
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint สำหรับสมัครสมาชิก
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // @Valid จะสั่งให้ Spring ตรวจสอบ DTO (เช่น @NotBlank, @Email)
        // @RequestBody บอกให้ Spring แปลง JSON ที่ส่งมาเป็น object RegisterRequest

        try {
            authService.registerUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (IllegalStateException e) {
            // (ในระบบจริง เราควรสร้าง Exception Handler ที่ดีกว่านี้)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint สำหรับเข้าสู่ระบบ
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        // เรียก AuthService เพื่อ Login
        AuthResponse authResponse = authService.loginUser(loginRequest);

        // ส่ง Token กลับไป (Status 200 OK)
        return ResponseEntity.ok(authResponse);
    }
}