package sv7.setec.api_hotelrental.Feature.hotels;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sv7.setec.api_hotelrental.Feature.hotels.Dtos.HotelImageRequestDto;
import sv7.setec.api_hotelrental.Feature.hotels.Dtos.HotelImageResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotel-images")
@RequiredArgsConstructor
@Tag(name = "Hotel Images", description = "Endpoints for hotel image uploads and management")
public class HotelImageController {

    private final HotelImageService hotelImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Upload image", description = "Upload a file and attach it to a hotel")
    public ResponseEntity<HotelImageResponseDto> uploadImage(
            @Parameter(description = "ID of the hotel to attach the image to", required = true)
            @RequestParam("hotelId") Integer hotelId,

            @Parameter(description = "Image file to upload", required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary")))
            @RequestParam("file") MultipartFile file,

            @Parameter(description = "Set as the primary hotel banner (true/false)")
            @RequestParam(value = "isBanner", required = false, defaultValue = "false") Boolean isBanner,

            @Parameter(description = "Display order sequence index")
            @RequestParam(value = "orderIndex", required = false) Integer orderIndex
    ) {
        HotelImageRequestDto requestDto = HotelImageRequestDto.builder()
                .hotelId(hotelId)
                .file(file)
                .isBanner(isBanner)
                .orderIndex(orderIndex)
                .build();

        HotelImageResponseDto response = hotelImageService.addImageToHotel(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get images by hotel", description = "Fetch all images belonging to a specific hotel")
    public ResponseEntity<List<HotelImageResponseDto>> getImagesByHotel(@PathVariable Integer hotelId) {
        return ResponseEntity.ok(hotelImageService.getImagesByHotelId(hotelId));
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete image", description = "Remove an image by its ID")
    public ResponseEntity<Void> deleteImage(@PathVariable Integer imageId) {
        hotelImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}