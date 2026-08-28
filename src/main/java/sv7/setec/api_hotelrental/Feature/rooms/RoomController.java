package sv7.setec.api_hotelrental.Feature.rooms;

import sv7.setec.api_hotelrental.Feature.rooms.Dtos.*;
import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    // 1. Get all rooms
    @GetMapping
    public ResponseEntity<PageResponse<RoomResponseDto>> getAllRooms(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) Long roomTypeId,
            @RequestParam(required = false) RoomStatus status,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return ResponseEntity.ok(roomService.getAllRooms(hotelId, roomTypeId, status, roomNumber, page, size, sortBy, sortDir));
    }

    // 2. Get room by ID
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDto> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    // 3. Create room
    @PostMapping
    public ResponseEntity<RoomResponseDto> createRoom(@Valid @RequestBody RoomRequestDto requestDto) {
        RoomResponseDto createdRoom = roomService.createRoom(requestDto);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

    // 4. Edit room
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDto> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequestDto requestDto
    ) {
        return ResponseEntity.ok(roomService.updateRoom(id, requestDto));
    }

    // 5. Patch room status
    @PatchMapping("/{id}/status")
    public ResponseEntity<RoomResponseDto> patchRoomStatus(
            @PathVariable Long id,
            @Valid @RequestBody RoomStatusUpdateDto statusDto
    ) {
        return ResponseEntity.ok(roomService.updateRoomStatus(id, statusDto));
    }

    // 6. Soft delete room
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteRoom(@PathVariable Long id) {
        roomService.softDeleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}