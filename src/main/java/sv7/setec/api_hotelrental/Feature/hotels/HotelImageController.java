package sv7.setec.api_hotelrental.Feature.hotels;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv7.setec.api_hotelrental.Feature.hotels.Dtos.HotelImageRequestDto;
import sv7.setec.api_hotelrental.Feature.hotels.Dtos.HotelImageResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotel-images")
@RequiredArgsConstructor
public class HotelImageController {

    private final HotelImageService hotelImageService;

    // GET /api/v1/hotel-images/hotel/{hotelId}
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<HotelImageResponseDto>> getImagesByHotel(@PathVariable Integer hotelId) {
        return ResponseEntity.ok(hotelImageService.getImagesByHotelId(hotelId));
    }

    // POST /api/v1/hotel-images
    @PostMapping
    public ResponseEntity<HotelImageResponseDto> addImage(@RequestBody HotelImageRequestDto requestDto) {
        HotelImageResponseDto response = hotelImageService.addImageToHotel(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // DELETE /api/v1/hotel-images/{imageId}
    @DeleteMapping("/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable Integer imageId) {
        hotelImageService.deleteImage(imageId);
        return ResponseEntity.ok("Image deleted successfully");
    }
}