package sv7.setec.api_hotelrental.Feature.rooms;

import sv7.setec.api_hotelrental.Feature.rooms.Dtos.*;
import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;

public interface RoomService {
    PageResponse<RoomResponseDto> getAllRooms(
            Long hotelId,
            Long roomTypeId,
            RoomStatus status,
            String roomNumber,
            int page,
            int size,
            String sortBy,
            String sortDir
    );
    RoomResponseDto getRoomById(Long id);
    RoomResponseDto createRoom(RoomRequestDto requestDto);
    RoomResponseDto updateRoom(Long id, RoomRequestDto requestDto);
    RoomResponseDto updateRoomStatus(Long id, RoomStatusUpdateDto statusDto);
    void softDeleteRoom(Long id);
}