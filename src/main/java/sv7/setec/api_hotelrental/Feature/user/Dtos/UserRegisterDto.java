package sv7.setec.api_hotelrental.Feature.user.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import sv7.setec.api_hotelrental.Feature.enums.Role;

@Data
public class UserRegisterDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "Unique username", example = "john_doe")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Unique email address", example = "john.doe@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character"
    )
    @Schema(description = "Password with minimum 8 characters, including uppercase, lowercase, number, and special character", example = "Password@123")
    private String password;

    @NotNull(message = "Role is required")
    @Schema(description = "User authorization role", example = "CUSTOMER")
    private Role role;

    @Pattern(regexp = "^(\\+?[0-9]{8,15})?$", message = "Phone number must be between 8 and 15 digits with an optional '+' prefix")
    @Schema(description = "Optional contact phone number", example = "+85512345678")
    private String phoneNumber;
}