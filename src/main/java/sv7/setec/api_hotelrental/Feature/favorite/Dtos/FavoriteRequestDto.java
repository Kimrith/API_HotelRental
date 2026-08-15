package Feature.favorite.Dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRequestDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    private Long hotelId; // Optional if favoriting a room

    private Long roomId;  // Optional if favoriting a hotel
}