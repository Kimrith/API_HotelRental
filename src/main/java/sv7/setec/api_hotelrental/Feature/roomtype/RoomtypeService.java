package sv7.setec.api_hotelrental.Feature.roomtype;

import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;
import sv7.setec.api_hotelrental.Feature.roomtype.Dtos.RoomTypeRequestDto;
import sv7.setec.api_hotelrental.Feature.roomtype.Dtos.RoomTypeResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface RoomtypeService {

    RoomTypeResponseDto createRoomType(RoomTypeRequestDto requestDto);

    RoomTypeResponseDto getRoomTypeById(Long id);

    List<RoomTypeResponseDto> getAllRoomTypes(LocalDate checkInDate, LocalDate checkOutDate);

    List<RoomTypeResponseDto> getRoomTypesByHotelId(Long hotelId);

    // Added method
    List<RoomTypeResponseDto> getRoomTypesByStatus(RoomStatus status);

    // Added method
    List<RoomTypeResponseDto> getRoomTypesByHotelIdAndStatus(Long hotelId, RoomStatus status);

    List<RoomTypeResponseDto> searchRoomTypesByName(String name);

    RoomTypeResponseDto updateRoomType(Long id, RoomTypeRequestDto requestDto);

    RoomTypeResponseDto updateRoomTypeStatus(Long id, RoomStatus status);

    void deleteRoomType(Long id);

    List<RoomTypeResponseDto> getRoomTypesWithAvailability(Long hotelId, LocalDate checkInDate, LocalDate checkOutDate);
}