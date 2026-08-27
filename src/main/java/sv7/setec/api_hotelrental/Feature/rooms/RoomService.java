package sv7.setec.api_hotelrental.Feature.rooms;

import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;
import sv7.setec.api_hotelrental.Feature.rooms.Dtos.RoomRequestDto;
import sv7.setec.api_hotelrental.Feature.rooms.Dtos.RoomResponseDto;

import java.util.List;

public interface RoomService {

    RoomResponseDto createRoom(RoomRequestDto requestDto);

    RoomResponseDto getRoomById(Long id);

    List<RoomResponseDto> getAllRooms();

    List<RoomResponseDto> getRoomsByHotelId(Long hotelId);

    List<RoomResponseDto> getRoomsByRoomTypeId(Long roomTypeId);

    List<RoomResponseDto> getRoomsByHotelAndStatus(Long hotelId, RoomStatus status);

    RoomResponseDto updateRoom(Long id, RoomRequestDto requestDto);

    RoomResponseDto updateRoomStatus(Long id, RoomStatus status);

    void deleteRoom(Long id);
}