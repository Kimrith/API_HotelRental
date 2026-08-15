package sv7.setec.api_hotelrental.Feature.review.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private Long userId;
    private Long hotelId;
    private Long roomId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}