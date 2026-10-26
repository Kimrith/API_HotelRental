package sv7.setec.api_hotelrental.Feature.payment.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BakongQrResponseDto {
    private String qrString;
    private String md5;
    private BigDecimal amount;
    private String currency;
    private String bakongId;
    private String merchantName;
    private String merchantCity;
}