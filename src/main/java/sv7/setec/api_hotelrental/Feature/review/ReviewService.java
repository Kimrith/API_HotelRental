package sv7.setec.api_hotelrental.Feature.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv7.setec.api_hotelrental.Feature.bookings.BookingRepository;
import sv7.setec.api_hotelrental.Feature.bookings.models.Booking;
import sv7.setec.api_hotelrental.Feature.enums.BookingStatus;
import sv7.setec.api_hotelrental.Feature.hotels.HotelRepository;
import sv7.setec.api_hotelrental.Feature.hotels.models.Hotel;
import sv7.setec.api_hotelrental.Feature.review.Dtos.ReviewRequestDto;
import sv7.setec.api_hotelrental.Feature.review.Dtos.ReviewResponseDto;
import sv7.setec.api_hotelrental.Feature.review.models.Review;
import sv7.setec.api_hotelrental.Feature.user.UserRepository;
import sv7.setec.api_hotelrental.Feature.user.models.User;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;

    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto requestDto) {
        // 1. Verify if booking exists
        Booking booking = bookingRepository.findById(requestDto.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + requestDto.getBookingId()));

        // 2. Verify if user exists
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + requestDto.getUserId()));

        // 3. Verify if hotel exists
        Hotel hotel = hotelRepository.findById(requestDto.getHotelId())
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with ID: " + requestDto.getHotelId()));

        // 4. Booking must belong to the user
        if (!booking.getCustomerId().equals(user.getId())) {
            throw new RuntimeException("Booking does not belong to the user attempting to review");
        }

        // 5. Booking must belong to the hotel
        if (!booking.getHotelId().equals(hotel.getId())) {
            throw new RuntimeException("Booking does not belong to the hotel being reviewed");
        }

        // 6. Booking must be in FINISHED status to be reviewed
        if (booking.getStatus() != BookingStatus.FINISHED) {
            throw new RuntimeException("Cannot review a booking that is not finished");
        }

        // 7. Check if review already exists for this booking (due to unique/OneToOne mapping)
        if (reviewRepository.existsByBookingId(requestDto.getBookingId())) {
            throw new RuntimeException("Review already exists for this booking");
        }

        Review review = Review.builder()
                .booking(booking)
                .user(user)
                .hotel(hotel)
                .rating(requestDto.getRating())
                .comment(requestDto.getComment())
                .build();

        Review saved = reviewRepository.save(review);
        return mapToResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByHotel(Integer hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new EntityNotFoundException("Hotel not found with ID: " + hotelId);
        }
        return reviewRepository.findByHotelId(hotelId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        return reviewRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReviewByBooking(Long bookingId) {
        Review review = reviewRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found for booking ID: " + bookingId));
        return mapToResponseDto(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new EntityNotFoundException("Review not found with ID: " + id);
        }
        reviewRepository.deleteById(id);
    }

    private ReviewResponseDto mapToResponseDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .bookingId(review.getBooking().getId())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .hotelId(review.getHotel().getId())
                .hotelName(review.getHotel().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
