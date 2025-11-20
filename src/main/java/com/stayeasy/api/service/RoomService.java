package com.stayeasy.api.service;

import com.stayeasy.api.dto.RoomRequest;
import com.stayeasy.api.dto.RoomResponse;
import com.stayeasy.api.model.Room;
import com.stayeasy.api.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * Logic สำหรับการสร้างห้องพักใหม่ (Admin)
     */
    @Transactional
    public RoomResponse createRoom(RoomRequest roomRequest) {
        // 1. ตรวจสอบว่าเลขห้องซ้ำหรือไม่ (Optional แต่แนะนำ)
        // (ในตัวอย่างนี้ เราสมมติว่า @Column(unique=true) ใน Entity จะจัดการให้)

        // 2. สร้าง Entity 'Room' จาก DTO 'RoomRequest'
        Room room = new Room();
        room.setRoomNumber(roomRequest.roomNumber());
        room.setRoomType(roomRequest.roomType());
        room.setPricePerNight(roomRequest.pricePerNight());
        room.setDescription(roomRequest.description());

        // 3. บันทึกลงฐานข้อมูล
        Room savedRoom = roomRepository.save(room);

        // 4. แปลง Entity ที่บันทึกแล้ว กลับเป็น DTO 'RoomResponse' เพื่อส่งกลับ
        return mapToRoomResponse(savedRoom);
    }

    /**
     * Logic สำหรับการดึงข้อมูลห้องพักทั้งหมด
     */
    @Transactional(readOnly = true) // (readOnly = true ช่วยเพิ่มประสิทธิภาพในการอ่าน)
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll() // ดึง 'Room' (Entity) ทั้งหมด
                .stream()                 // แปลงเป็น Stream
                .map(this::mapToRoomResponse) // แปลง 'Room' แต่ละตัวเป็น 'RoomResponse'
                .collect(Collectors.toList()); // รวบรวมกลับเป็น List
    }

    /**
     * Helper Method
     * สำหรับแปลง Entity 'Room' ไปเป็น DTO 'RoomResponse'
     */
    public RoomResponse mapToRoomResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getPricePerNight(),
                room.getDescription()
        );
    }
}