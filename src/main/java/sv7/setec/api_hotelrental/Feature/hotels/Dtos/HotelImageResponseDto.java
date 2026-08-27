package sv7.setec.api_hotelrental.Feature.hotels.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelImageResponseDto {
    private Integer id;
    private Integer hotelId;
    private String imageUrl;
    private Boolean isBanner;
    private Integer orderIndex;
}