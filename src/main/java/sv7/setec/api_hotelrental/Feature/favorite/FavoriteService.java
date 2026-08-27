package sv7.setec.api_hotelrental.Feature.favorite;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv7.setec.api_hotelrental.Feature.favorite.Dtos.FavoriteRequestDto;
import sv7.setec.api_hotelrental.Feature.favorite.Dtos.FavoriteResponseDto;
import sv7.setec.api_hotelrental.Feature.favorite.models.Favorite;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    // Get all favorites (or filtered by user)
    public List<FavoriteResponseDto> getAllFavorites() {
        return favoriteRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // Add to favorites
    public FavoriteResponseDto addFavorite(FavoriteRequestDto requestDto) {
        // Prevent duplicate favorite
        if (favoriteRepository.existsByUserIdAndHotelId(requestDto.getUserId(), requestDto.getHotelId())) {
            throw new RuntimeException("Hotel is already in favorites");
        }

        Favorite favorite = Favorite.builder()
                .userId(requestDto.getUserId())
                .hotelId(requestDto.getHotelId())
                .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);
        return mapToResponseDto(savedFavorite);
    }

    // Remove favorite by ID
    @Transactional
    public void removeFavorite(Long id) {
        if (!favoriteRepository.existsById(id)) {
            throw new RuntimeException("Favorite not found with id: " + id);
        }
        favoriteRepository.deleteById(id);
    }

    // Helper mapper
    private FavoriteResponseDto mapToResponseDto(Favorite favorite) {
        return FavoriteResponseDto.builder()
                .id(favorite.getId())
                .userId(favorite.getUserId())
                .hotelId(favorite.getHotelId())
                .build();
    }
}