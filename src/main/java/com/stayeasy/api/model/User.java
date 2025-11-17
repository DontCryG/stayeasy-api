package com.stayeasy.api.model;

import jakarta.persistence.*; // ใช้ jakarta.* สำหรับ Spring Boot 3+
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity // บอก Spring ว่านี่คือ Entity (ตารางในฐานข้อมูล)
@Table(name = "app_users") // ตั้งชื่อตาราง (หลีกเลี่ยงคำว่า 'user' ที่อาจซ้ำกับ SQL)
public class User implements UserDetails {

    @Id // นี่คือ Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ให้ Database สร้าง ID ให้อัตโนมัติ
    private Long id;

    @Column(nullable = false, unique = true) // ห้ามว่าง และ ห้ามซ้ำ
    private String username; // เราจะใช้อีเมลเป็น username

    @Column(nullable = false)
    private String password; // รหัสผ่านที่ถูกเข้ารหัส (Hashed)

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING) // เก็บค่า Enum เป็น String (เช่น "ROLE_USER")
    @Column(nullable = false)
    private Role role;

    // --- Constructors ---
    public User() {
        // Constructor ว่างสำหรับ JPA
    }

    public User(String username, String password, String fullName, Role role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

// --- Getters and Setters (จำเป็นสำหรับ JPA) ---
    // (IntelliJ สามารถ Generate ให้ได้: Alt+Insert -> Getter and Setter)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // (นี่คือ 2 ตัวที่เราเพิ่มเข้ามา)
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // --- Override Methods จาก UserDetails (สำหรับ Spring Security) ---

    @Override
    public String getUsername() {
        return this.username; // ใช้ field username ของเรา
    }

    @Override
    public String getPassword() {
        return this.password; // ใช้ field password ของเรา
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // คืนค่า Role ของผู้ใช้
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // true = บัญชียังไม่หมดอายุ
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // true = บัญชีไม่ถูกล็อค
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // true = รหัสผ่านยังไม่หมดอายุ
    }

    @Override
    public boolean isEnabled() {
        return true; // true = บัญชีเปิดใช้งาน
    }

    // --- (Optional but recommended) equals() and hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}