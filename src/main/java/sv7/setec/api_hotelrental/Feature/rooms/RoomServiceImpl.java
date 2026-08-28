package sv7.setec.api_hotelrental.Feature.rooms;

import sv7.setec.api_hotelrental.Feature.rooms.Dtos.*;
import sv7.setec.api_hotelrental.Feature.rooms.models.Room;
import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;
import sv7.setec.api_hotelrental.Feature.roomtype.models.RoomType;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    // Inject RoomTypeRepository if you need to fetch/validate the RoomType entity
    // private final RoomTypeRepository roomTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoomResponseDto> getAllRooms(
            Long hotelId,
            Long roomTypeId,
            RoomStatus status,
            String roomNumber,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name())
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Room> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hotelId != null) {
                predicates.add(cb.equal(root.get("hotelId"), hotelId));
            }
            if (roomTypeId != null) {
                predicates.add(cb.equal(root.get("roomType").get("id"), roomTypeId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (roomNumber != null && !roomNumber.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("roomNumber")), "%" + roomNumber.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Room> roomsPage = roomRepository.findAll(spec, pageable);

        List<RoomResponseDto> content = roomsPage.getContent().stream()
                .map(RoomResponseDto::fromEntity)
                .collect(Collectors.toList());

        return PageResponse.<RoomResponseDto>builder()
                .content(content)
                .pageNumber(roomsPage.getNumber())
                .pageSize(roomsPage.getSize())
                .totalElements(roomsPage.getTotalElements())
                .totalPages(roomsPage.getTotalPages())
                .isLast(roomsPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponseDto getRoomById(Long id) {
        Room room = findRoomById(id);
        return RoomResponseDto.fromEntity(room);
    }

    @Override
    @Transactional
    public RoomResponseDto createRoom(RoomRequestDto requestDto) {
        RoomType roomType = new RoomType();
        roomType.setId(requestDto.getRoomTypeId());

        Room room = Room.builder()
                .hotelId(requestDto.getHotelId())
                .roomType(roomType)
                .roomNumber(requestDto.getRoomNumber())
                .status(requestDto.getStatus() != null ? requestDto.getStatus() : RoomStatus.AVAILABLE)
                .isDeleted(false)
                .build();

        return RoomResponseDto.fromEntity(roomRepository.save(room));
    }

    @Override
    @Transactional
    public RoomResponseDto updateRoom(Long id, RoomRequestDto requestDto) {
        Room room = findRoomById(id);

        RoomType roomType = new RoomType();
        roomType.setId(requestDto.getRoomTypeId());

        room.setHotelId(requestDto.getHotelId());
        room.setRoomType(roomType);
        room.setRoomNumber(requestDto.getRoomNumber());
        if (requestDto.getStatus() != null) {
            room.setStatus(requestDto.getStatus());
        }

        return RoomResponseDto.fromEntity(roomRepository.save(room));
    }

    @Override
    @Transactional
    public RoomResponseDto updateRoomStatus(Long id, RoomStatusUpdateDto statusDto) {
        Room room = findRoomById(id);
        room.setStatus(statusDto.getStatus());
        return RoomResponseDto.fromEntity(roomRepository.save(room));
    }

    @Override
    @Transactional
    public void softDeleteRoom(Long id) {
        Room room = findRoomById(id);
        // Using Hibernate's @SQLDelete executes an UPDATE, or set manually:
        room.setIsDeleted(true);
        roomRepository.save(room);
    }

    private Room findRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with ID: " + id));
    }
}