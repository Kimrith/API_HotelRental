package sv7.setec.api_hotelrental.Feature.booking_audits;

import sv7.setec.api_hotelrental.Feature.booking_audits.Dtos.BookingAuditRequestDto;
import sv7.setec.api_hotelrental.Feature.booking_audits.Dtos.BookingAuditResponseDto;
import sv7.setec.api_hotelrental.Feature.booking_audits.Dtos.PageResponse;

public interface BookingAuditService {
    BookingAuditResponseDto logAction(BookingAuditRequestDto requestDto);
    PageResponse<BookingAuditResponseDto> getLogsByBooking(Long bookingId, int page, int size);
    PageResponse<BookingAuditResponseDto> getAllLogs(int page, int size);
}
