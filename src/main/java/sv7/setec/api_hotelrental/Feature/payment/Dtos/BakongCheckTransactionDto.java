package sv7.setec.api_hotelrental.Feature.payment.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BakongCheckTransactionDto {
    private boolean isPaid;
    private int responseCode;
    private String responseMessage;
    private Object data;
}