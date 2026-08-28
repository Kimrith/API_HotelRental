package sv7.setec.api_hotelrental.Feature.rooms.Dtos;

import sv7.setec.api_hotelrental.Feature.rooms.models.Room;
import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomResponseDto {
    private Long id;
    private Long hotelId;
    private Long roomTypeId;
    private String roomNumber;
    private RoomStatus status;

    public static RoomResponseDto fromEntity(Room room) {
        return RoomResponseDto.builder()
                .id(room.getId())
                .hotelId(room.getHotelId())
                .roomTypeId(room.getRoomType() != null ? room.getRoomType().getId() : null)
                .roomNumber(room.getRoomNumber())
                .status(room.getStatus())
                .build();
    }
}