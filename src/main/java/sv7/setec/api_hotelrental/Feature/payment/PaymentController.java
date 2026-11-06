package sv7.setec.api_hotelrental.Feature.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv7.setec.api_hotelrental.Feature.payment.Dtos.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@Tag(name = "Payment", description = "Endpoints for Bakong KHQR payment and order processing")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/generate-qr")
    @Operation(summary = "Generate Bakong KHQR", description = "Generates a dynamic Bakong KHQR code and MD5 hash for payment")
    public ResponseEntity<BakongQrResponseDto> generateQr(@Valid @RequestBody GenerateQrRequestDto request) {
        return ResponseEntity.ok(paymentService.generateQr(request));
    }

    @PostMapping("/check-transaction")
    @Operation(summary = "Check Bakong Transaction", description = "Verifies payment status with NBC Bakong API using transaction MD5 hash")
    public ResponseEntity<BakongCheckTransactionDto> checkTransaction(@Valid @RequestBody CheckTransactionRequestDto request) {
        return ResponseEntity.ok(paymentService.checkTransaction(request.getMd5()));
    }

    @PostMapping("/order")
    @Operation(summary = "Place order with payment", description = "Processes order. If KHQR, requires paid transaction before saving to DB. If CASH, saves with PENDING status.")
    public ResponseEntity<PaymentOrderResponseDto> processOrderPayment(@Valid @RequestBody PaymentOrderRequestDto request) {
        PaymentOrderResponseDto response = paymentService.processOrderPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/checkout")
    @Operation(summary = "Alias for place order with payment")
    public ResponseEntity<PaymentOrderResponseDto> checkout(@Valid @RequestBody PaymentOrderRequestDto request) {
        PaymentOrderResponseDto response = paymentService.processOrderPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all payments")
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get payments for booking")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentsByBookingId(bookingId));
    }
}