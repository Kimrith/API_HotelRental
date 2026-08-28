package sv7.setec.api_hotelrental.Feature.review;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv7.setec.api_hotelrental.Feature.review.Dtos.ReviewRequestDto;
import sv7.setec.api_hotelrental.Feature.review.Dtos.ReviewResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponseDto> createReview(@Valid @RequestBody ReviewRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(requestDto));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByHotel(@PathVariable Integer hotelId) {
        return ResponseEntity.ok(reviewService.getReviewsByHotel(hotelId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ReviewResponseDto> getReviewByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(reviewService.getReviewByBooking(bookingId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
