package sv7.setec.api_hotelrental.Feature.roomtype.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeResponseDto {

    @Schema(description = "Room Type ID", example = "1")
    private Long id;

    @Schema(description = "Hotel ID", example = "10")
    private Long hotelId;

    @Schema(description = "Room type name", example = "Deluxe King Suite")
    private String name;

    @Schema(description = "Base price per night", example = "120.00")
    private BigDecimal basePrice;

    @Schema(description = "Maximum guests capacity", example = "2")
    private Integer maxGuests;

    @Schema(description = "Detailed description", example = "Spacious ocean-view room with king bed")
    private String description;

    @Schema(description = "Search keywords", example = "ocean view, king bed, balcony")
    private String searchKeywords;
}