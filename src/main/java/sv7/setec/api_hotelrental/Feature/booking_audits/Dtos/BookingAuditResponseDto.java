package sv7.setec.api_hotelrental.Feature.booking_audits.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingAuditResponseDto {
    private Long id;
    private Long bookingId;
    private Long actorUserId;
    private String actorUsername;
    private String action;
    private String reason;
    private LocalDateTime createdAt;
}
