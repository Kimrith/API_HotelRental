package sv7.setec.api_hotelrental.Feature.payment.Dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sv7.setec.api_hotelrental.Feature.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderRequestDto {

    @NotNull(message = "customerId is required")
    private Long customerId;

    @NotNull(message = "hotelId is required")
    private Integer hotelId;

    @NotNull(message = "roomTypeId is required")
    private Long roomTypeId;

    @NotNull(message = "checkInDate is required")
    @FutureOrPresent(message = "checkInDate cannot be in the past")
    private LocalDate checkInDate;

    @NotNull(message = "checkOutDate is required")
    private LocalDate checkOutDate;

    @NotNull(message = "paymentMethod is required (KHQR or CASH)")
    private PaymentMethod paymentMethod;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    private BigDecimal amount;

    // Required for KHQR payment verification
    private String md5;
}