package sv7.setec.api_hotelrental.Feature.bookings.Dtos;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {

    @NotNull(message = "customerId is required")
    private Long customerId;

    @NotNull(message = "hotelId is required")
    private Integer hotelId;

    @NotNull(message = "roomTypeId is required")
    private Long roomTypeId;

    @NotNull(message = "checkInDate is required")
    @FutureOrPresent(message = "checkInDate cannot be in the past")
    private LocalDate checkInDate;

    @NotNull(message = "checkOutDate is required")
    private LocalDate checkOutDate;
}