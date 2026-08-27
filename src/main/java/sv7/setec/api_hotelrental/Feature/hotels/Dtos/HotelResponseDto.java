package sv7.setec.api_hotelrental.Feature.hotels.Dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponseDto {
    private Integer id;
    private String name;
    private String description;
    private String address;
    private String city;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer depositPercentage;
    private String status;
    private LocalDateTime createdAt;
    private List<ImageResponseDto> images;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageResponseDto {
        private Integer id;
        private String imageUrl;
        private Boolean isBanner;
        private Integer orderIndex;
    }
}