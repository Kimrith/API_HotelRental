package sv7.setec.api_hotelrental.Feature.bookings.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sv7.setec.api_hotelrental.Feature.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {

    private Long id;

    private Long customerId;

    private Integer hotelId;

    private Long roomTypeId;

    private Long assignedRoomId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private BigDecimal totalPrice;

    private BigDecimal depositAmount;

    private BookingStatus status;

    private LocalDateTime createdAt;
}