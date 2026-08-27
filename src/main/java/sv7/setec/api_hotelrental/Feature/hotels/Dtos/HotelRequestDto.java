package sv7.setec.api_hotelrental.Feature.hotels.Dtos;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelRequestDto {
    private String name;
    private String description;
    private String address;
    private String city;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer depositPercentage;
    private String status;
    private List<ImageDto> images;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageDto {
        private String imageUrl;
        private Boolean isBanner;
        private Integer orderIndex;
    }
}