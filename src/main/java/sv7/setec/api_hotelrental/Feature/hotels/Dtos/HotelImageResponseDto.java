package sv7.setec.api_hotelrental.Feature.hotels.Dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelImageResponseDto {
    private Integer id;
    private Integer hotelId;
    private String imageUrl;
    private Boolean isBanner;
    private Integer orderIndex;
}