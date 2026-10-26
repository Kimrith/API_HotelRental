package sv7.setec.api_hotelrental.Feature.payment.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sv7.setec.api_hotelrental.Feature.bookings.Dtos.BookingResponseDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponseDto {
    private String message;
    private BookingResponseDto booking;
    private PaymentResponseDto payment;
}