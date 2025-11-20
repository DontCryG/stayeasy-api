package com.stayeasy.api.config;

import com.stayeasy.api.model.Role;
import com.stayeasy.api.model.User;
import com.stayeasy.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component // บอก Spring ว่านี่คือ Bean
public class DataInitializer implements CommandLineRunner {

    // เราต้องการ 2 อย่างนี้เพื่อสร้าง Admin
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Method นี้จะถูก Spring Boot เรียกทำงานอัตโนมัติ 1 ครั้ง
     * หลังจากที่ Application เริ่มทำงานเสร็จสมบูรณ์
     */
    @Override
    public void run(String... args) throws Exception {
        // สร้าง Admin user (ถ้ายังไม่มี)

        String adminEmail = "admin@stayeasy.com";

        // 1. ตรวจสอบก่อนว่ามี Admin user หรือยัง
        if (userRepository.findByUsername(adminEmail).isEmpty()) {

            // 2. ถ้ายังไม่มี ให้สร้างใหม่
            User adminUser = new User();
            adminUser.setUsername(adminEmail);
            adminUser.setFullName("Admin User");
            adminUser.setPassword(passwordEncoder.encode("adminpassword")); // เข้ารหัสผ่าน
            adminUser.setRole(Role.ROLE_ADMIN); // ตั้งค่าเป็น ADMIN

            // 3. บันทึกลง Database
            userRepository.save(adminUser);

            System.out.println("--- Admin user created successfully ---");
        } else {
            System.out.println("--- Admin user already exists ---");
        }
    }
}