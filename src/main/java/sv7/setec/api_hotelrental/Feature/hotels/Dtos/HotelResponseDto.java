package sv7.setec.api_hotelrental.Feature.hotels.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelResponseDto {
    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private String address;
    private String city;
    private String status;
    private LocalDateTime createdAt;
}