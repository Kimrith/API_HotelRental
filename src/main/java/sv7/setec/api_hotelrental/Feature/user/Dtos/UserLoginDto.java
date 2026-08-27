package sv7.setec.api_hotelrental.Feature.user.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDto {

    @NotBlank(message = "Username or email is required")
    @Schema(description = "Username or email address", example = "john_doe")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "User account password", example = "Password@123")
    private String password;
}