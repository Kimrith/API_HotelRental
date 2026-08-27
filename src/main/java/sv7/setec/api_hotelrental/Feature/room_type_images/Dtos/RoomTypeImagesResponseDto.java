package sv7.setec.api_hotelrental.Feature.room_type_images.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeImagesResponseDto {

    @Schema(description = "Image ID", example = "1")
    private Long id; // Use Integer if your entity ID is Integer

    @Schema(description = "Room Type ID this image belongs to", example = "5")
    private Long roomTypeId; // Use Integer if your RoomType ID is Integer

    @Schema(description = "URL of the uploaded image", example = "https://your-domain.com/uploads/rooms/deluxe-1.jpg")
    private String imageUrl;

    @Schema(description = "Indicates whether this is the primary cover image", example = "true")
    private Boolean isPrimary;
}