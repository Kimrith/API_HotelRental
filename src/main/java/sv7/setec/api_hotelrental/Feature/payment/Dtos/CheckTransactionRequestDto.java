package sv7.setec.api_hotelrental.Feature.payment.Dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckTransactionRequestDto {
    @NotBlank(message = "md5 is required")
    private String md5;
}