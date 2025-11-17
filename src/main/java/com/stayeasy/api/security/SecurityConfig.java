package com.stayeasy.api.security;

import com.stayeasy.api.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // บอก Spring ว่านี่คือไฟล์ Configuration
@EnableWebSecurity // เปิดใช้งาน Spring Security
public class SecurityConfig {

    // เราจะ Inject 2 อย่างที่เราสร้างไว้
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // --- (A) Beans ที่จำเป็น ---

    /**
     * Bean สำหรับเข้ารหัสรหัสผ่าน
     * เราใช้ BCrypt ที่แข็งแกร่งและเป็นมาตรฐาน
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean สำหรับจัดการการ Authentication
     * มันจะใช้ UserDetailsService และ PasswordEncoder
     */
    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        // ส่ง userDetailsService เข้าไปใน constructor เลย
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder); // (บรรทัด setUserDetailsService ลบไป)
        return new ProviderManager(provider);
    }

    /**
     * Bean สำหรับสร้าง Filter (ตัวกรอง) ที่จะอ่าน Token จากทุก Request
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }

    // --- (B) กฎการเข้าถึง (Security Filter Chain) ---

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. ปิด CSRF (เพราะเราใช้ JWT ไม่ได้ใช้ Session/Cookie)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. ตั้งค่า Session Management เป็น STATELESS (สำคัญมากสำหรับ JWT)
                // บอก Spring Security ว่า "ห้ามสร้าง Session"
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. กำหนดกฎการเข้าถึง URL (Authorize Requests)
                .authorizeHttpRequests(authorize -> authorize
                        // (Whitelist) URL ที่อนุญาตให้เข้าได้โดยไม่ต้อง Login
                        .requestMatchers("/api/auth/**").permitAll() // API Login/Register
                        .requestMatchers("/h2-console/**").permitAll() // H2 Console (ถ้าใช้)

                        // (Admin Role) URL ที่ต้องใช้สิทธิ์ Admin
                        .requestMatchers("/api/rooms/**").hasRole("ADMIN")

                        // URL ที่เหลือทั้งหมด ต้อง Login (Authenticated)
                        .anyRequest().authenticated()
                )

                // 4. (สำหรับ H2 Console) ปิด Frame Options เพื่อให้ H2 Console ทำงานได้
                .headers(headers -> headers.frameOptions(frameConfig -> frameConfig.disable()))

                // 5. เพิ่ม Filter (JwtAuthenticationFilter) ของเรา
                // ให้ทำงาน "ก่อน" Filter มาตรฐาน (UsernamePasswordAuthenticationFilter)
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}