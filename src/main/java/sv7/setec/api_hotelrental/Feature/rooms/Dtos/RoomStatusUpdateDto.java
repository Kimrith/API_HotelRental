package sv7.setec.api_hotelrental.Feature.rooms.Dtos;

import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomStatusUpdateDto {
    @NotNull(message = "Status cannot be null")
    private RoomStatus status;
}