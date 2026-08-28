package sv7.setec.api_hotelrental.Feature.roomtype;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv7.setec.api_hotelrental.Feature.bookings.BookingRepository;
import sv7.setec.api_hotelrental.Feature.enums.BookingStatus;
import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;
import sv7.setec.api_hotelrental.Feature.enums.RoomTypeStatus;
import sv7.setec.api_hotelrental.Feature.hotels.HotelRepository;
import sv7.setec.api_hotelrental.Feature.hotels.models.Hotel;
import sv7.setec.api_hotelrental.Feature.rooms.RoomRepository;
import sv7.setec.api_hotelrental.Feature.roomtype.Dtos.RoomTypeRequestDto;
import sv7.setec.api_hotelrental.Feature.roomtype.Dtos.RoomTypeResponseDto;
import sv7.setec.api_hotelrental.Feature.roomtype.models.RoomType;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomtypeServiceImpl implements RoomtypeService {

    private static final EnumSet<BookingStatus> ACTIVE_RESERVATIONS = EnumSet.of(
            BookingStatus.PENDING,
            BookingStatus.APPROVED,
            BookingStatus.CONFIRMED,
            BookingStatus.CHECKED_IN
    );

    private final RoomtypeRepository roomtypeRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeResponseDto> getRoomTypesWithAvailability(
            Long hotelId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {
        List<RoomType> roomTypes = roomtypeRepository.findByHotel_Id(hotelId.intValue());

        return roomTypes.stream().map(rt -> {
            RoomTypeResponseDto dto = toResponseDto(rt);


            // 1. Total physical rooms under this category
            long totalRooms = roomRepository.countByHotelIdAndRoomTypeId(hotelId, rt.getId());

            // 2. Count active overlapping bookings
            long bookedRooms = bookingRepository.countOverlappingBookings(
                    hotelId.intValue(),
                    rt.getId(),
                    checkInDate,
                    checkOutDate,
                    ACTIVE_RESERVATIONS
            );

            long available = Math.max(0, totalRooms - bookedRooms);

            // 3. Mark status as BOOKED if 0 rooms remain for these dates
            if (totalRooms == 0 || available == 0) {
                dto.setStatus(RoomStatus.BOOKED);
            } else {
                dto.setStatus(RoomStatus.AVAILABLE);
            }

            return dto;
        }).toList();
    }

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
                .status(requestDto.getStatus() != null ? RoomTypeStatus.valueOf(requestDto.getStatus().name()) : RoomTypeStatus.AVAILABLE)
                .description(requestDto.getDescription())
                .searchKeywords(requestDto.getSearchKeywords())
                .build();

        return toResponseDto(roomtypeRepository.save(roomType));
    }

    @Override
    @Transactional(readOnly = true)
    public RoomTypeResponseDto getRoomTypeById(Long id) {
        return toResponseDto(roomtypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RoomType not found with ID: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeResponseDto> getAllRoomTypes(LocalDate checkInDate, LocalDate checkOutDate) {
        // Default to today and tomorrow if dates are not provided in request
        LocalDate fromDate = (checkInDate != null) ? checkInDate : LocalDate.now();
        LocalDate toDate = (checkOutDate != null) ? checkOutDate : fromDate.plusDays(1);

        if (!toDate.isAfter(fromDate)) {
            toDate = fromDate.plusDays(1);
        }

        final LocalDate finalFromDate = fromDate;
        final LocalDate finalToDate = toDate;

        List<RoomType> roomTypes = roomtypeRepository.findAll();

        return roomTypes.stream().map(rt -> {
            RoomTypeResponseDto dto = toResponseDto(rt);

            // If room category was manually set to MAINTENANCE or UNAVAILABLE by admin
            if (rt.getStatus() != RoomTypeStatus.AVAILABLE) {
                dto.setTotalRooms(0L);
                dto.setAvailableRooms(0L);
                return dto;
            }

            // 1. Total physical room count
            long totalRooms = roomRepository.countByHotelIdAndRoomTypeId(
                    rt.getHotel().getId().longValue(),
                    rt.getId()
            );

            // 2. Total active bookings for this date range
            long bookedRooms = bookingRepository.countOverlappingBookings(
                    rt.getHotel().getId(),
                    rt.getId(),
                    finalFromDate,
                    finalToDate,
                    ACTIVE_RESERVATIONS
            );

            long availableRooms = Math.max(0, totalRooms - bookedRooms);

            dto.setTotalRooms(totalRooms);
            dto.setAvailableRooms(availableRooms);

            // 3. Dynamically set status based on real-time availability
            if (totalRooms == 0 || availableRooms == 0) {
                dto.setStatus(RoomStatus.BOOKED);
            } else {
                dto.setStatus(RoomStatus.AVAILABLE);
            }

            return dto;
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeResponseDto> getRoomTypesByHotelId(Long hotelId) {
        return roomtypeRepository.findByHotel_Id(hotelId.intValue()).stream().map(this::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeResponseDto> getRoomTypesByStatus(RoomStatus status) {
        return roomtypeRepository.findByStatus(status)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeResponseDto> getRoomTypesByHotelIdAndStatus(Long hotelId, RoomStatus status) {
        return roomtypeRepository.findByHotel_IdAndStatus(hotelId.intValue(), status)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeResponseDto> searchRoomTypesByName(String name) {
        return roomtypeRepository.findByNameContainingIgnoreCase(name).stream().map(this::toResponseDto).toList();
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
        if (requestDto.getStatus() != null) {
            roomType.setStatus(RoomTypeStatus.valueOf(requestDto.getStatus().name()));
        }
        roomType.setDescription(requestDto.getDescription());
        roomType.setSearchKeywords(requestDto.getSearchKeywords());

        return toResponseDto(roomtypeRepository.save(roomType));
    }

    @Override
    @Transactional
    public RoomTypeResponseDto updateRoomTypeStatus(Long id, RoomStatus status) {
        RoomType roomType = roomtypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RoomType not found with ID: " + id));
        roomType.setStatus(RoomTypeStatus.valueOf(status.name()));
        return toResponseDto(roomtypeRepository.save(roomType));
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
                .status(entity.getStatus() != null ? RoomStatus.valueOf(entity.getStatus().name()) : null)
                .description(entity.getDescription())
                .searchKeywords(entity.getSearchKeywords())
                .build();
    }
}