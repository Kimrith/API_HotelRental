package sv7.setec.api_hotelrental.Feature.bookings;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv7.setec.api_hotelrental.Feature.bookings.Dtos.BookingRequestDto;
import sv7.setec.api_hotelrental.Feature.bookings.Dtos.BookingResponseDto;
import sv7.setec.api_hotelrental.Feature.enums.BookingStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(
    name = "Bookings",
    description = "Hotel booking management"
)
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Create booking")
    public ResponseEntity<BookingResponseDto> createBooking(
            @Valid
            @RequestBody BookingRequestDto request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        bookingService.createBooking(
                                request
                        )
                );
    }

    @GetMapping
    @Operation(summary = "Get all bookings")
    public ResponseEntity<List<BookingResponseDto>>
    getAllBookings() {

        return ResponseEntity.ok(
                bookingService.getAllBookings()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID")
    public ResponseEntity<BookingResponseDto>
    getBookingById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                bookingService.getBookingById(id)
        );
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get customer bookings")
    public ResponseEntity<List<BookingResponseDto>>
    getByCustomer(
            @PathVariable Long customerId
    ) {
        return ResponseEntity.ok(
                bookingService.getByCustomer(
                        customerId
                )
        );
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get hotel bookings")
    public ResponseEntity<List<BookingResponseDto>>
    getByHotel(
            @PathVariable Integer hotelId
    ) {
        return ResponseEntity.ok(
                bookingService.getByHotel(hotelId)
        );
    }

    @GetMapping("/availability")
    @Operation(summary = "Check room availability")
    public ResponseEntity<Map<String, Long>>
    getAvailability(

            @RequestParam Integer hotelId,

            @RequestParam Long roomTypeId,

            @RequestParam
            @DateTimeFormat(
                iso = DateTimeFormat.ISO.DATE
            )
            LocalDate checkInDate,

            @RequestParam
            @DateTimeFormat(
                iso = DateTimeFormat.ISO.DATE
            )
            LocalDate checkOutDate
    ) {
        long availableRooms =
                bookingService
                        .getAvailableRoomCount(
                                hotelId,
                                roomTypeId,
                                checkInDate,
                                checkOutDate
                        );

        return ResponseEntity.ok(
                Map.of(
                        "availableRooms",
                        availableRooms
                )
        );
    }

    @PatchMapping("/{id}/approve")
    @Operation(
        summary = "Approve booking and assign room"
    )
    public ResponseEntity<BookingResponseDto>
    approveBooking(

            @PathVariable Long id,

            @RequestParam Long roomId
    ) {
        return ResponseEntity.ok(
                bookingService.approveBooking(
                        id,
                        roomId
                )
        );
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update booking status")
    public ResponseEntity<BookingResponseDto>
    updateStatus(

            @PathVariable Long id,

            @RequestParam BookingStatus status
    ) {
        return ResponseEntity.ok(
                bookingService.updateStatus(
                        id,
                        status
                )
        );
    }
}