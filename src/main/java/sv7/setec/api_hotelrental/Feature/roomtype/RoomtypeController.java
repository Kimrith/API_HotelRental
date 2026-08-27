package sv7.setec.api_hotelrental.Feature.roomtype;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv7.setec.api_hotelrental.Feature.roomtype.Dtos.RoomTypeRequestDto;
import sv7.setec.api_hotelrental.Feature.roomtype.Dtos.RoomTypeResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/room-types")
@RequiredArgsConstructor
@Tag(name = "Room Types", description = "Endpoints for managing room types")
public class RoomtypeController {

    private final RoomtypeService roomtypeService;

    @PostMapping
    @Operation(summary = "Create a new room type")
    public ResponseEntity<RoomTypeResponseDto> createRoomType(@Valid @RequestBody RoomTypeRequestDto requestDto) {
        RoomTypeResponseDto response = roomtypeService.createRoomType(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all room types")
    public ResponseEntity<List<RoomTypeResponseDto>> getAllRoomTypes() {
        return ResponseEntity.ok(roomtypeService.getAllRoomTypes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room type by ID")
    public ResponseEntity<RoomTypeResponseDto> getRoomTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(roomtypeService.getRoomTypeById(id));
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get room types for a specific hotel")
    public ResponseEntity<List<RoomTypeResponseDto>> getRoomTypesByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomtypeService.getRoomTypesByHotelId(hotelId));
    }

    @GetMapping("/search")
    @Operation(summary = "Search room types by name")
    public ResponseEntity<List<RoomTypeResponseDto>> searchRoomTypes(@RequestParam String name) {
        return ResponseEntity.ok(roomtypeService.searchRoomTypesByName(name));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing room type")
    public ResponseEntity<RoomTypeResponseDto> updateRoomType(
            @PathVariable Long id,
            @Valid @RequestBody RoomTypeRequestDto requestDto) {
        return ResponseEntity.ok(roomtypeService.updateRoomType(id, requestDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room type")
    public ResponseEntity<Void> deleteRoomType(@PathVariable Long id) {
        roomtypeService.deleteRoomType(id);
        return ResponseEntity.noContent().build();
    }
}