package com.stayeasy.api.service;

import com.stayeasy.api.dto.BookingRequest;
import com.stayeasy.api.dto.BookingResponse;
import com.stayeasy.api.model.Booking;
import com.stayeasy.api.model.Room;
import com.stayeasy.api.model.User;
import com.stayeasy.api.repository.BookingRepository;
import com.stayeasy.api.repository.RoomRepository;
import com.stayeasy.api.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomService roomService; // (เราจะใช้ 'mapToRoomResponse' ที่เราสร้างไว้)

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          UserRepository userRepository,
                          RoomService roomService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roomService = roomService;
    }

    /**
     * Logic สำหรับสร้างการจองใหม่ (User)
     */
    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest) {
        // 1. ตรวจสอบความถูกต้องของวันที่
        if (bookingRequest.checkInDate().isAfter(bookingRequest.checkOutDate()) ||
                bookingRequest.checkInDate().isEqual(bookingRequest.checkOutDate())) {
            throw new IllegalStateException("Check-out date must be after check-in date");
        }

        // 2. ค้นหา Room (Entity) จาก roomId
        Room room = roomRepository.findById(bookingRequest.roomId())
                .orElseThrow(() -> new IllegalStateException("Room not found"));

        // 3. (สำคัญ) ตรวจสอบว่าห้องว่างหรือไม่
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                room,
                bookingRequest.checkInDate(),
                bookingRequest.checkOutDate()
        );

        if (!overlappingBookings.isEmpty()) {
            // ถ้า List ไม่ว่าง = มีการจองที่ทับซ้อน = ห้องไม่ว่าง
            throw new IllegalStateException("Room is not available for the selected dates");
        }

        // 4. (สำคัญ) ดึงข้อมูล User (ผู้จอง)
        // เราจะดึง User ที่ Login อยู่ (จาก Token)
        User currentUser = getCurrentUser();

        // 5. สร้าง Entity 'Booking'
        Booking booking = new Booking();
        booking.setUser(currentUser);
        booking.setRoom(room);
        booking.setCheckInDate(bookingRequest.checkInDate());
        booking.setCheckOutDate(bookingRequest.checkOutDate());

        // 6. บันทึกลงฐานข้อมูล
        Booking savedBooking = bookingRepository.save(booking);

        // 7. แปลงเป็น Response DTO เพื่อส่งกลับ
        return mapToBookingResponse(savedBooking);
    }

    /**
     * Logic สำหรับดึงประวัติการจองของ User ที่ Login อยู่
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings() {
        User currentUser = getCurrentUser();

        return bookingRepository.findByUser(currentUser)
                .stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    // --- Helper Methods ---

    /**
     * ดึง Entity 'User' ของคนที่ Login อยู่ในปัจจุบัน
     */
    private User getCurrentUser() {
        // ดึง Authentication object จาก SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        String username = authentication.getName(); // (นี่คืออีเมล)

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * แปลง Entity 'Booking' ไปเป็น DTO 'BookingResponse'
     */
    private BookingResponse mapToBookingResponse(Booking booking) {
        // เราใช้ RoomService (mapToRoomResponse) ที่มีอยู่แล้ว มาช่วยแปลง Room
        return new BookingResponse(
                booking.getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                roomService.mapToRoomResponse(booking.getRoom())
        );
    }
}