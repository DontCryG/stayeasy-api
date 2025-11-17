package com.stayeasy.api.service;

import com.stayeasy.api.dto.AuthResponse;
import com.stayeasy.api.dto.LoginRequest;
import com.stayeasy.api.dto.RegisterRequest;
import com.stayeasy.api.model.Role;
import com.stayeasy.api.model.User;
import com.stayeasy.api.repository.UserRepository;
import com.stayeasy.api.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import นี้สำคัญ

@Service
public class AuthService {

    // เราต้องการเครื่องมือ 4 อย่างนี้
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Logic สำหรับการสมัครสมาชิก
     */
    @Transactional // ทำให้ Method นี้ทำงานภายใต้ Transaction (ถ้า Error จะ Rollback)
    public void registerUser(RegisterRequest registerRequest) {
        // 1. ตรวจสอบว่ามี Email นี้ในระบบหรือยัง
        if (userRepository.findByUsername(registerRequest.email()).isPresent()) {
            throw new IllegalStateException("Email already in use");
            // (ในระบบจริง ควรใช้ Custom Exception ที่ดีกว่านี้)
        }

        // 2. สร้าง User object ใหม่
        User user = new User();
        user.setUsername(registerRequest.email());
        user.setFullName(registerRequest.fullName());

        // 3. (สำคัญ) เข้ารหัสรหัสผ่านก่อนบันทึก
        user.setPassword(passwordEncoder.encode(registerRequest.password()));

        // 4. กำหนด Role (ผู้ใช้ใหม่ทุกคนเป็น USER)
        user.setRole(Role.ROLE_USER);

        // 5. บันทึกลงฐานข้อมูล
        userRepository.save(user);
    }

    /**
     * Logic สำหรับการเข้าสู่ระบบ
     */
    public AuthResponse loginUser(LoginRequest loginRequest) {
        // 1. (สำคัญ) ใช้ AuthenticationManager ตรวจสอบ Email/Password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        // 2. ถ้าผ่าน (ไม่ throw exception) ให้ตั้งค่าใน SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. สร้าง JWT Token จาก Authentication object
        String token = jwtTokenProvider.generateToken(authentication);

        // 4. ส่ง Token กลับไปใน AuthResponse
        return new AuthResponse(token);
    }
}