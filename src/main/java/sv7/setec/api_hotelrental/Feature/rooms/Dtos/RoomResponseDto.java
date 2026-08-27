package sv7.setec.api_hotelrental.Feature.rooms.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponseDto {

    @Schema(description = "Unique identifier of the physical room", example = "1")
    private Long id;

    @Schema(description = "ID of the hotel this room belongs to", example = "10")
    private Long hotelId;

    @Schema(description = "ID of the associated room type", example = "2")
    private Long roomTypeId;

    @Schema(description = "Name of the associated room type", example = "Deluxe King Suite")
    private String roomTypeName;

    @Schema(description = "Physical room/door number", example = "101")
    private String roomNumber;

    @Schema(description = "Current room availability status", example = "AVAILABLE")
    private RoomStatus status;
}