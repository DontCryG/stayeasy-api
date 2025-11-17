package com.stayeasy.api.repository;

import com.stayeasy.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // บอก Spring ว่านี่คือ Bean ที่จัดการฐานข้อมูล
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository<User, Long> หมายถึง:
    // - User: เราจะจัดการกับ Entity ชื่อ User
    // - Long: Primary Key (id) ของ User เป็นชนิด Long

    // เราจะเพิ่ม "Custom Query Method" ที่นี่
    // Spring Data JPA ฉลาดพอที่จะสร้าง query ให้อัตโนมัติจากชื่อ method

    /**
     * ค้นหาผู้ใช้ด้วย username
     *
     * @param username (ที่เราใช้อีเมล)
     * @return Optional<User> เพราะผู้ใช้อาจจะไม่มีอยู่จริง
     */
    Optional<User> findByUsername(String username);
}