package sv7.setec.api_hotelrental.Feature.roomtype;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv7.setec.api_hotelrental.Feature.hotels.HotelRepository;
import sv7.setec.api_hotelrental.Feature.hotels.models.Hotel;
import sv7.setec.api_hotelrental.Feature.roomtype.Dtos.RoomTypeRequestDto;
import sv7.setec.api_hotelrental.Feature.roomtype.Dtos.RoomTypeResponseDto;
import sv7.setec.api_hotelrental.Feature.roomtype.models.RoomType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomtypeServiceImpl implements RoomtypeService {

    private final RoomtypeRepository roomtypeRepository;
    private final HotelRepository hotelRepository;

    @Override
    @Transactional
    public RoomTypeResponseDto createRoomType(RoomTypeRequestDto requestDto) {
        Hotel hotel = hotelRepository.findById(requestDto.getHotelId().intValue())
                .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + requestDto.getHotelId()));

        RoomType roomType = RoomType.builder()
                .hotel(hotel)
                .name(requestDto.getName())
                .basePrice(requestDto.getBasePrice())
                .maxGuests(requestDto.getMaxGuests())
                .description(requestDto.getDescription())
                .searchKeywords(requestDto.getSearchKeywords())
                .build();

        RoomType saved = roomtypeRepository.save(roomType);
        return toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomTypeResponseDto getRoomTypeById(Long id) {
        RoomType roomType = roomtypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RoomType not found with ID: " + id));
        return toResponseDto(roomType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeResponseDto> getAllRoomTypes() {
        return roomtypeRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeResponseDto> getRoomTypesByHotelId(Long hotelId) {
        return roomtypeRepository.findByHotel_Id(hotelId.intValue())
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeResponseDto> searchRoomTypesByName(String name) {
        return roomtypeRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public RoomTypeResponseDto updateRoomType(Long id, RoomTypeRequestDto requestDto) {
        RoomType roomType = roomtypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RoomType not found with ID: " + id));

        Hotel hotel = hotelRepository.findById(requestDto.getHotelId().intValue())
                .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + requestDto.getHotelId()));

        roomType.setHotel(hotel);
        roomType.setName(requestDto.getName());
        roomType.setBasePrice(requestDto.getBasePrice());
        roomType.setMaxGuests(requestDto.getMaxGuests());
        roomType.setDescription(requestDto.getDescription());
        roomType.setSearchKeywords(requestDto.getSearchKeywords());

        RoomType updated = roomtypeRepository.save(roomType);
        return toResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteRoomType(Long id) {
        if (!roomtypeRepository.existsById(id)) {
            throw new RuntimeException("RoomType not found with ID: " + id);
        }
        roomtypeRepository.deleteById(id);
    }

    private RoomTypeResponseDto toResponseDto(RoomType entity) {
        return RoomTypeResponseDto.builder()
                .id(entity.getId())
                .hotelId(entity.getHotel() != null ? entity.getHotel().getId().longValue() : null)
                .name(entity.getName())
                .basePrice(entity.getBasePrice())
                .maxGuests(entity.getMaxGuests())
                .description(entity.getDescription())
                .searchKeywords(entity.getSearchKeywords())
                .build();
    }
}