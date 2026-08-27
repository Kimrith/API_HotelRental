package sv7.setec.api_hotelrental.Feature.room_type_images;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sv7.setec.api_hotelrental.Feature.room_type_images.Dtos.RoomTypeImagesRequestDto;
import sv7.setec.api_hotelrental.Feature.room_type_images.Dtos.RoomTypeImagesResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/room-type-images")
@RequiredArgsConstructor
@Tag(name = "Room Type Images", description = "Endpoints for managing room type photos")
public class RoomTypeImagesController {

    private final RoomTypeImagesService roomTypeImagesService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Upload image files from local device for a room type")
    public ResponseEntity<List<RoomTypeImagesResponseDto>> uploadImages(
            @Parameter(description = "ID of the room type", required = true)
            @RequestParam("roomTypeId") Long roomTypeId,

            @Parameter(description = "Image files to upload", required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary")))
            @RequestParam("files") List<MultipartFile> files,

            @Parameter(description = "Index of the image to set as primary/cover (0-based)")
            @RequestParam(value = "primaryIndex", required = false, defaultValue = "0") Integer primaryIndex) {

        List<RoomTypeImagesResponseDto> response = roomTypeImagesService.uploadImages(roomTypeId, files, primaryIndex);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/url", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add an image using a file upload")
    public ResponseEntity<RoomTypeImagesResponseDto> addImageUrl(
            @RequestParam("roomTypeId") Long roomTypeId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPrimary", defaultValue = "false") Boolean isPrimary
    ) {
        RoomTypeImagesRequestDto requestDto = RoomTypeImagesRequestDto.builder()
                .roomTypeId(roomTypeId)
                .file(file)
                .isPrimary(isPrimary)
                .build();
        RoomTypeImagesResponseDto response = roomTypeImagesService.addImageUrl(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/room-type/{roomTypeId}")
    @Operation(summary = "Get all images for a specific room type")
    public ResponseEntity<List<RoomTypeImagesResponseDto>> getImagesByRoomTypeId(@PathVariable Long roomTypeId) {
        return ResponseEntity.ok(roomTypeImagesService.getImagesByRoomTypeId(roomTypeId));
    }

    @PatchMapping("/{id}/primary")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Set an image as the primary cover photo")
    public ResponseEntity<Void> setPrimaryImage(@PathVariable Long id) {
        roomTypeImagesService.setPrimaryImage(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an image by ID")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        roomTypeImagesService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}