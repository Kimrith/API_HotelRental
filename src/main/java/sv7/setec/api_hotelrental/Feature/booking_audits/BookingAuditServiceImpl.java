package sv7.setec.api_hotelrental.Feature.booking_audits;

import sv7.setec.api_hotelrental.Feature.booking_audits.Dtos.BookingAuditRequestDto;
import sv7.setec.api_hotelrental.Feature.booking_audits.Dtos.BookingAuditResponseDto;
import sv7.setec.api_hotelrental.Feature.booking_audits.Dtos.PageResponse;
import sv7.setec.api_hotelrental.Feature.booking_audits.models.BookingAuditLog;
import sv7.setec.api_hotelrental.Feature.bookings.BookingRepository;
import sv7.setec.api_hotelrental.Feature.bookings.models.Booking;
import sv7.setec.api_hotelrental.Feature.user.UserRepository;
import sv7.setec.api_hotelrental.Feature.user.models.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingAuditServiceImpl implements BookingAuditService {

    private final BookingAuditRepository bookingAuditRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingAuditResponseDto logAction(BookingAuditRequestDto requestDto) {
        Booking booking = bookingRepository.findById(requestDto.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + requestDto.getBookingId()));

        User actor = userRepository.findById(requestDto.getActorUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + requestDto.getActorUserId()));

        BookingAuditLog log = BookingAuditLog.builder()
                .booking(booking)
                .actor(actor)
                .action(requestDto.getAction())
                .reason(requestDto.getReason())
                .build();

        BookingAuditLog saved = bookingAuditRepository.save(log);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingAuditResponseDto> getLogsByBooking(Long bookingId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BookingAuditLog> logPage = bookingAuditRepository.findByBookingId(bookingId, pageable);
        return mapToPageResponse(logPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingAuditResponseDto> getAllLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BookingAuditLog> logPage = bookingAuditRepository.findAll(pageable);
        return mapToPageResponse(logPage);
    }

    private BookingAuditResponseDto mapToResponseDto(BookingAuditLog log) {
        return BookingAuditResponseDto.builder()
                .id(log.getId())
                .bookingId(log.getBooking().getId())
                .actorUserId(log.getActor().getId())
                .actorUsername(log.getActor().getUsername())
                .action(log.getAction())
                .reason(log.getReason())
                .createdAt(log.getCreatedAt())
                .build();
    }

    private PageResponse<BookingAuditResponseDto> mapToPageResponse(Page<BookingAuditLog> logPage) {
        List<BookingAuditResponseDto> content = logPage.getContent().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return PageResponse.<BookingAuditResponseDto>builder()
                .content(content)
                .pageNumber(logPage.getNumber())
                .pageSize(logPage.getSize())
                .totalElements(logPage.getTotalElements())
                .totalPages(logPage.getTotalPages())
                .isLast(logPage.isLast())
                .build();
    }
}
