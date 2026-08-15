package sv7.setec.api_hotelrental.Feature.user.Dtos;

import sv7.setec.api_hotelrental.Feature.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private String phoneNumber;
    private LocalDateTime createdAt;
}