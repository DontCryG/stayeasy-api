package com.stayeasy.api.security;

import com.stayeasy.api.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Filter นี้จะทำงาน "ครั้งเดียว" ต่อ 1 Request
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserDetailsServiceImpl userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. ดึง Token ออกมาจาก Request Header
            String jwt = getJwtFromRequest(request);

            // 2. ตรวจสอบ Token (Validate)
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

                // 3. ถ้า Token ถูกต้อง, ดึง username ออกมา
                String username = tokenProvider.getUsernameFromToken(jwt);

                // 4. โหลดข้อมูล User จาก Database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. สร้าง Authentication object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. (สำคัญ) ตั้งค่า Authentication ใน SecurityContext
                // บอก Spring Security ว่า "User นี้ Login แล้วนะ"
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // (ในระบบจริง ควร Log Error)
            logger.error("Could not set user authentication in security context", ex);
        }

        // 7. ส่ง Request/Response ไปยัง Filter ตัวถัดไป
        filterChain.doFilter(request, response);
    }

    /**
     * Helper method
     * ดึง Token จาก "Authorization" Header (ที่ขึ้นต้นด้วย "Bearer ")
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // ตัดคำว่า "Bearer " ออก
        }
        return null;
    }
}