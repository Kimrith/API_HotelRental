package sv7.setec.api_hotelrental.Feature.hotels;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv7.setec.api_hotelrental.Feature.hotels.Dtos.HotelRequestDto;
import sv7.setec.api_hotelrental.Feature.hotels.Dtos.HotelResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<List<HotelResponseDto>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelResponseDto> getHotelById(@PathVariable Integer id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @PostMapping
    public ResponseEntity<HotelResponseDto> createHotel(@RequestBody HotelRequestDto requestDto) {
        HotelResponseDto createdHotel = hotelService.createHotel(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHotel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHotel(@PathVariable Integer id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.ok("Hotel deleted successfully");
    }
}