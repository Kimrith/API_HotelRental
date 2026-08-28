package sv7.setec.api_hotelrental.Feature.booking_audits;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv7.setec.api_hotelrental.Feature.booking_audits.Dtos.BookingAuditRequestDto;
import sv7.setec.api_hotelrental.Feature.booking_audits.Dtos.BookingAuditResponseDto;
import sv7.setec.api_hotelrental.Feature.booking_audits.Dtos.PageResponse;

@RestController
@RequestMapping("/api/v1/booking-audits")
@RequiredArgsConstructor
public class BookingAuditController {

    private final BookingAuditService bookingAuditService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<BookingAuditResponseDto> logAction(@Valid @RequestBody BookingAuditRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingAuditService.logAction(requestDto));
    }

    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<PageResponse<BookingAuditResponseDto>> getLogsByBooking(
            @PathVariable Long bookingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(bookingAuditService.getLogsByBooking(bookingId, page, size));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<BookingAuditResponseDto>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(bookingAuditService.getAllLogs(page, size));
    }
}
