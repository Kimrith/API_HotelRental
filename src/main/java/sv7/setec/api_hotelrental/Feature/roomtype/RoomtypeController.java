package sv7.setec.api_hotelrental.Feature.roomtype;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;
import sv7.setec.api_hotelrental.Feature.roomtype.Dtos.RoomTypeRequestDto;
import sv7.setec.api_hotelrental.Feature.roomtype.Dtos.RoomTypeResponseDto;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/room-types")
@RequiredArgsConstructor
@Tag(name = "Room Types", description = "Endpoints for managing room types")
public class RoomtypeController {

    private final RoomtypeService roomTypeService;

    @PostMapping
    @Operation(summary = "Create a new room type")
    public ResponseEntity<RoomTypeResponseDto> createRoomType(@Valid @RequestBody RoomTypeRequestDto requestDto) {
        RoomTypeResponseDto response = roomTypeService.createRoomType(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all room types with dynamic availability")
    public ResponseEntity<List<RoomTypeResponseDto>> getAllRoomTypes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate
    ) {
        return ResponseEntity.ok(roomTypeService.getAllRoomTypes(checkInDate, checkOutDate));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a room type by ID")
    public ResponseEntity<RoomTypeResponseDto> getRoomTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(roomTypeService.getRoomTypeById(id));
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get all room types belonging to a hotel")
    public ResponseEntity<List<RoomTypeResponseDto>> getRoomTypesByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomTypeService.getRoomTypesByHotelId(hotelId));
    }

    @GetMapping("/hotel/{hotelId}/search-availability")
    @Operation(summary = "Search room types for a hotel with real-time status and remaining room counts")
    public ResponseEntity<List<RoomTypeResponseDto>> getAvailableRoomTypes(
            @PathVariable Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {
        return ResponseEntity.ok(roomTypeService.getRoomTypesWithAvailability(hotelId, checkInDate, checkOutDate));
    }

    @GetMapping("/status")
    @Operation(summary = "Get room types by status")
    public ResponseEntity<List<RoomTypeResponseDto>> getRoomTypesByStatus(@RequestParam RoomStatus status) {
        return ResponseEntity.ok(roomTypeService.getRoomTypesByStatus(status));
    }

    @GetMapping("/hotel/{hotelId}/status")
    @Operation(summary = "Get room types by hotel and status")
    public ResponseEntity<List<RoomTypeResponseDto>> getRoomTypesByHotelIdAndStatus(
            @PathVariable Long hotelId,
            @RequestParam RoomStatus status) {
        return ResponseEntity.ok(roomTypeService.getRoomTypesByHotelIdAndStatus(hotelId, status));
    }

    @GetMapping("/search")
    @Operation(summary = "Search room types by name")
    public ResponseEntity<List<RoomTypeResponseDto>> searchRoomTypes(@RequestParam String name) {
        return ResponseEntity.ok(roomTypeService.searchRoomTypesByName(name));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room type details")
    public ResponseEntity<RoomTypeResponseDto> updateRoomType(
            @PathVariable Long id,
            @Valid @RequestBody RoomTypeRequestDto requestDto) {
        return ResponseEntity.ok(roomTypeService.updateRoomType(id, requestDto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update room type status")
    public ResponseEntity<RoomTypeResponseDto> updateRoomTypeStatus(
            @PathVariable Long id,
            @RequestParam RoomStatus status) {
        return ResponseEntity.ok(roomTypeService.updateRoomTypeStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room type by ID")
    public ResponseEntity<Void> deleteRoomType(@PathVariable Long id) {
        roomTypeService.deleteRoomType(id);
        return ResponseEntity.noContent().build();
    }
}