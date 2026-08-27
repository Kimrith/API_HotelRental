package sv7.setec.api_hotelrental.Feature.room_type_images.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeImagesResponseDto {
    private Long id;
    private Long hotelId;
    private String roomName;
    private String roomNumber;
    private String roomType;
    private BigDecimal price;
    private Integer stock;
    private String description;
}