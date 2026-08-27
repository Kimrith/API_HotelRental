package sv7.setec.api_hotelrental.Feature.room_type_images.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeImagesRequestDto {

    @NotNull(message = "Room type ID is required")
    @Schema(description = "ID of the room type", example = "1")
    private Long roomTypeId;

    @NotNull(message = "Image file is required")
    @Schema(description = "Image file to upload", type = "string", format = "binary")
    private MultipartFile file;

    @Schema(description = "Indicates whether this image is the cover/primary image", example = "false")
    @Builder.Default
    private Boolean isPrimary = false;
}