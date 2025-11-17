package com.stayeasy.api.repository;

import com.stayeasy.api.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // JpaRepository<Room, Long> หมายถึง:
    // - Room: เราจะจัดการกับ Entity ชื่อ Room
    // - Long: Primary Key (id) ของ Room เป็นชนิด Long

    // ในอนาคต เราสามารถเพิ่ม method ค้นหาอื่นๆ ที่นี่ได้
    // เช่น findByRoomType(String roomType)
}