package sv7.setec.api_hotelrental.Feature.rooms;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;
import sv7.setec.api_hotelrental.Feature.rooms.Dtos.RoomRequestDto;
import sv7.setec.api_hotelrental.Feature.rooms.Dtos.RoomResponseDto;
import sv7.setec.api_hotelrental.Feature.rooms.models.Room;
import sv7.setec.api_hotelrental.Feature.roomtype.RoomtypeRepository;
import sv7.setec.api_hotelrental.Feature.roomtype.models.RoomType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomtypeRepository roomtypeRepository;

    @Override
    @Transactional
    public RoomResponseDto createRoom(RoomRequestDto requestDto) {
        RoomType roomType = roomtypeRepository.findById(requestDto.getRoomTypeId())
                .orElseThrow(() -> new RuntimeException("RoomType not found with ID: " + requestDto.getRoomTypeId()));

        if (roomRepository.existsByHotelIdAndRoomNumber(requestDto.getHotelId(), requestDto.getRoomNumber())) {
            throw new RuntimeException("Room number " + requestDto.getRoomNumber() + " already exists in this hotel");
        }

        Room room = new Room();
        room.setHotelId(requestDto.getHotelId());
        room.setRoomType(roomType);
        room.setRoomNumber(requestDto.getRoomNumber());
        room.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : RoomStatus.AVAILABLE);

        Room savedRoom = roomRepository.save(room);
        return toResponseDto(savedRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponseDto getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + id));
        return toResponseDto(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDto> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDto> getRoomsByHotelId(Long hotelId) {
        return roomRepository.findByHotelId(hotelId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDto> getRoomsByRoomTypeId(Long roomTypeId) {
        return roomRepository.findByRoomTypeId(roomTypeId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDto> getRoomsByHotelAndStatus(Long hotelId, RoomStatus status) {
        return roomRepository.findByHotelIdAndStatus(hotelId, status)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public RoomResponseDto updateRoom(Long id, RoomRequestDto requestDto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + id));

        RoomType roomType = roomtypeRepository.findById(requestDto.getRoomTypeId())
                .orElseThrow(() -> new RuntimeException("RoomType not found with ID: " + requestDto.getRoomTypeId()));

        // Check uniqueness only if room number or hotel changed
        if (!room.getRoomNumber().equals(requestDto.getRoomNumber()) || !room.getHotelId().equals(requestDto.getHotelId())) {
            if (roomRepository.existsByHotelIdAndRoomNumber(requestDto.getHotelId(), requestDto.getRoomNumber())) {
                throw new RuntimeException("Room number " + requestDto.getRoomNumber() + " already exists in this hotel");
            }
        }

        room.setHotelId(requestDto.getHotelId());
        room.setRoomType(roomType);
        room.setRoomNumber(requestDto.getRoomNumber());
        if (requestDto.getStatus() != null) {
            room.setStatus(requestDto.getStatus());
        }

        Room updatedRoom = roomRepository.save(room);
        return toResponseDto(updatedRoom);
    }

    @Override
    @Transactional
    public RoomResponseDto updateRoomStatus(Long id, RoomStatus status) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + id));

        room.setStatus(status);
        Room updatedRoom = roomRepository.save(room);
        return toResponseDto(updatedRoom);
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Room not found with ID: " + id);
        }
        roomRepository.deleteById(id);
    }

    private RoomResponseDto toResponseDto(Room entity) {
        return RoomResponseDto.builder()
                .id(entity.getId())
                .hotelId(entity.getHotelId())
                .roomTypeId(entity.getRoomType() != null ? entity.getRoomType().getId() : null)
                .roomTypeName(entity.getRoomType() != null ? entity.getRoomType().getName() : null)
                .roomNumber(entity.getRoomNumber())
                .status(entity.getStatus())
                .build();
    }
}