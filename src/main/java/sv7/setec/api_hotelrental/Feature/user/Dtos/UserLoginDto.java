package sv7.setec.api_hotelrental.Feature.user.Dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDto {

    @NotBlank(message = "Username or email is required")
    private String identifier; // Can accept either username or email

    @NotBlank(message = "Password is required")
    private String password;
}