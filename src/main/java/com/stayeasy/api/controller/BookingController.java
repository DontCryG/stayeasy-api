package com.stayeasy.api.controller;

import com.stayeasy.api.dto.BookingRequest;
import com.stayeasy.api.dto.BookingResponse;
import com.stayeasy.api.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings") // API ทั้งหมดในนี้จะขึ้นต้นด้วย /api/bookings
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Endpoint สำหรับสร้างการจองใหม่ (User Only)
     * POST /api/bookings
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest bookingRequest) {
        // (เราไม่ต้องเช็ก Role 'USER' ที่นี่
        // เพราะ SecurityConfig ของเราตั้งค่าว่า 'anyRequest().authenticated()'
        // ขอแค่ Login (มี Token) ก็เรียกได้)

        try {
            BookingResponse newBooking = bookingService.createBooking(bookingRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(newBooking);
        } catch (IllegalStateException e) {
            // (ในระบบจริง เราควรสร้าง Exception Handler ที่ดีกว่านี้)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Endpoint สำหรับดูประวัติการจองของฉัน (My Bookings)
     * GET /api/bookings/my-history
     */
    @GetMapping("/my-history")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {
        List<BookingResponse> bookings = bookingService.getMyBookings();
        return ResponseEntity.ok(bookings);
    }
}