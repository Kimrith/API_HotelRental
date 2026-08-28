package sv7.setec.api_hotelrental.Feature.review;

import sv7.setec.api_hotelrental.Feature.review.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByHotelId(Integer hotelId);
    List<Review> findByUserId(Long userId);
    Optional<Review> findByBookingId(Long bookingId);
    boolean existsByBookingId(Long bookingId);
}
