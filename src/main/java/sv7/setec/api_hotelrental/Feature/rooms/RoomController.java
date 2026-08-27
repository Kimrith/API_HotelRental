package sv7.setec.api_hotelrental.Feature.rooms;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;
import sv7.setec.api_hotelrental.Feature.rooms.Dtos.RoomRequestDto;
import sv7.setec.api_hotelrental.Feature.rooms.Dtos.RoomResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "Endpoints for managing hotel rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @Operation(summary = "Create a new room")
    public ResponseEntity<RoomResponseDto> createRoom(@Valid @RequestBody RoomRequestDto requestDto) {
        RoomResponseDto response = roomService.createRoom(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all rooms")
    public ResponseEntity<List<RoomResponseDto>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a room by its ID")
    public ResponseEntity<RoomResponseDto> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get all rooms belonging to a specific hotel")
    public ResponseEntity<List<RoomResponseDto>> getRoomsByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomsByHotelId(hotelId));
    }

    @GetMapping("/room-type/{roomTypeId}")
    @Operation(summary = "Get all rooms for a specific room type")
    public ResponseEntity<List<RoomResponseDto>> getRoomsByRoomTypeId(@PathVariable Long roomTypeId) {
        return ResponseEntity.ok(roomService.getRoomsByRoomTypeId(roomTypeId));
    }

    @GetMapping("/hotel/{hotelId}/status")
    @Operation(summary = "Get rooms by hotel ID and availability status")
    public ResponseEntity<List<RoomResponseDto>> getRoomsByHotelAndStatus(
            @PathVariable Long hotelId,
            @RequestParam RoomStatus status) {
        return ResponseEntity.ok(roomService.getRoomsByHotelAndStatus(hotelId, status));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room details")
    public ResponseEntity<RoomResponseDto> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequestDto requestDto) {
        return ResponseEntity.ok(roomService.updateRoom(id, requestDto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update room status (AVAILABLE, BOOKED, MAINTENANCE, etc.)")
    public ResponseEntity<RoomResponseDto> updateRoomStatus(
            @PathVariable Long id,
            @RequestParam RoomStatus status) {
        return ResponseEntity.ok(roomService.updateRoomStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room by its ID")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}