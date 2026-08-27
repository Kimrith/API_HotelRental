package sv7.setec.api_hotelrental.Feature.hotels;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HotelResponseDto> createHotel(@Valid @RequestBody HotelRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(requestDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHotel(@PathVariable Integer id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }
}