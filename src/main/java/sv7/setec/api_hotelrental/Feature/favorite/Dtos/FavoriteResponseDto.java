package sv7.setec.api_hotelrental.Feature.favorite.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResponseDto {
    private Long id;
    private Long userId;
    private Long hotelId;
    private Long roomId;
    private LocalDateTime createdAt;
}