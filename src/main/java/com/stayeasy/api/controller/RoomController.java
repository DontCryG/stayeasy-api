package com.stayeasy.api.controller;

import com.stayeasy.api.dto.RoomRequest;
import com.stayeasy.api.dto.RoomResponse;
import com.stayeasy.api.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms") // API ทั้งหมดในนี้จะขึ้นต้นด้วย /api/rooms
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * Endpoint สำหรับสร้างห้องพักใหม่ (Admin Only)
     * POST /api/rooms
     */
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomRequest roomRequest) {
        // (เราไม่ต้องเช็ก Role 'ADMIN' ที่นี่
        // เพราะ SecurityConfig ของเราจัดการบล็อก '/api/rooms/**' ให้อัตโนมัติแล้ว)

        RoomResponse newRoom = roomService.createRoom(roomRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRoom);
    }

    /**
     * Endpoint สำหรับดึงข้อมูลห้องพักทั้งหมด (Authenticated User)
     * GET /api/rooms
     */
    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        List<RoomResponse> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }
}