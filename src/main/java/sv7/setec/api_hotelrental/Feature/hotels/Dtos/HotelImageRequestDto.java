package sv7.setec.api_hotelrental.Feature.hotels.Dtos;

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
public class HotelImageRequestDto {

    @NotNull(message = "Hotel ID is required")
    private Integer hotelId;

    @NotNull(message = "Image file is required")
    private MultipartFile file;

    private Boolean isBanner;

    private Integer orderIndex;
}