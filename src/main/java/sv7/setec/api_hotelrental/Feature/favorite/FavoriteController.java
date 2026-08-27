package sv7.setec.api_hotelrental.Feature.favorite;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv7.setec.api_hotelrental.Feature.favorite.Dtos.FavoriteRequestDto;
import sv7.setec.api_hotelrental.Feature.favorite.Dtos.FavoriteResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 1. GET ALL FAVORITES -> GET /api/v1/favorites
    @GetMapping
    public ResponseEntity<List<FavoriteResponseDto>> getFavorites() {
        return ResponseEntity.ok(favoriteService.getAllFavorites());
    }

    // 2. ADD FAVORITE -> POST /api/v1/favorites
    @PostMapping
    public ResponseEntity<FavoriteResponseDto> addFavorite(@RequestBody FavoriteRequestDto requestDto) {
        FavoriteResponseDto response = favoriteService.addFavorite(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 3. REMOVE FAVORITE -> DELETE /api/v1/favorites/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeFavorite(@PathVariable Long id) {
        favoriteService.removeFavorite(id);
        return ResponseEntity.ok("Favorite removed successfully");
    }
}