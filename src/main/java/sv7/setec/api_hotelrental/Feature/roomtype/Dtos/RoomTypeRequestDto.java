package sv7.setec.api_hotelrental.Feature.roomtype.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeRequestDto {

    @NotNull(message = "Hotel ID is required")
    @Schema(description = "ID of the hotel this room type belongs to", example = "1")
    private Long hotelId;

    @NotBlank(message = "Room type name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Schema(description = "Name of the room type", example = "Deluxe King Suite")
    private String name;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than zero")
    @Schema(description = "Base price per night", example = "120.00")
    private BigDecimal basePrice;

    @NotNull(message = "Max guests is required")
    @Min(value = 1, message = "Maximum guests must be at least 1")
    @Schema(description = "Maximum occupancy of guests", example = "2")
    private Integer maxGuests;

    @Schema(description = "Detailed room type description", example = "Spacious ocean-view room with king bed and balcony")
    private String description;

    @Schema(description = "Comma-separated search keywords", example = "ocean view, king bed, wifi, breakfast")
    private String searchKeywords;
}