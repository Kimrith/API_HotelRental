package sv7.setec.api_hotelrental.Feature.booking_audits.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingAuditRequestDto {
    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Actor User ID is required")
    private Long actorUserId;

    @NotBlank(message = "Action is required")
    private String action;

    private String reason;
}
