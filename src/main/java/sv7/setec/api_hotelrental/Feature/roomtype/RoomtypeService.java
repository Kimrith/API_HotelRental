package sv7.setec.api_hotelrental.Feature.roomtype;

import sv7.setec.api_hotelrental.Feature.roomtype.Dtos.RoomTypeRequestDto;
import sv7.setec.api_hotelrental.Feature.roomtype.Dtos.RoomTypeResponseDto;

import java.util.List;

public interface RoomtypeService {

    RoomTypeResponseDto createRoomType(RoomTypeRequestDto requestDto);

    RoomTypeResponseDto getRoomTypeById(Long id);

    List<RoomTypeResponseDto> getAllRoomTypes();

    List<RoomTypeResponseDto> getRoomTypesByHotelId(Long hotelId);

    List<RoomTypeResponseDto> searchRoomTypesByName(String name);

    RoomTypeResponseDto updateRoomType(Long id, RoomTypeRequestDto requestDto);

    void deleteRoomType(Long id);
}