package sv7.setec.api_hotelrental.Feature.rooms.Dtos;

import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomRequestDto {
    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    @NotNull(message = "Room Type ID is required")
    private Long roomTypeId;

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    private RoomStatus status;
}