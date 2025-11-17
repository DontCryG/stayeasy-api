package com.stayeasy.api.service;

import com.stayeasy.api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // บอก Spring ว่านี่คือ Service Bean
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // เราใช้ Constructor Injection (วิธีที่ Spring แนะนำ)
    // Spring จะ inject UserRepository ให้อัตโนมัติ
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * นี่คือ method ที่ Spring Security จะเรียกใช้เมื่อมีคนพยายาม Login
     *
     * @param username (ในระบบของเราคือ Email)
     * @return UserDetails (ซึ่งก็คือ Entity 'User' ของเราที่ implement UserDetails ไว้)
     * @throws UsernameNotFoundException ถ้าไม่พบผู้ใช้
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ค้นหาผู้ใช้จาก Repository
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: " + username));
    }
}