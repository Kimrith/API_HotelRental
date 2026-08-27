package sv7.setec.api_hotelrental.Feature.favorite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv7.setec.api_hotelrental.Feature.favorite.models.Favorite;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Fetch all favorites for a specific user
    List<Favorite> findByUserId(Long userId);

    // Check or find existing favorite by user and hotel (assuming hotelId)
    Optional<Favorite> findByUserIdAndHotelId(Long userId, Long hotelId);

    // Delete favorite by userId and hotelId
    void deleteByUserIdAndHotelId(Long userId, Long hotelId);

    // Check existence
    boolean existsByUserIdAndHotelId(Long userId, Long hotelId);
}