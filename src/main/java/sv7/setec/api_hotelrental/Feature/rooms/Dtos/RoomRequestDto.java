package sv7.setec.api_hotelrental.Feature.rooms.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequestDto {

    @NotNull(message = "Hotel ID is required")
    @Schema(description = "ID of the hotel this room belongs to", example = "1")
    private Long hotelId;

    @NotNull(message = "Room type ID is required")
    @Schema(description = "ID of the room category/type", example = "2")
    private Long roomTypeId;

    @NotBlank(message = "Room number is required")
    @Schema(description = "Unique room identifier/door number", example = "101")
    private String roomNumber;

    @Schema(description = "Status of the room", example = "AVAILABLE")
    private RoomStatus status;
}