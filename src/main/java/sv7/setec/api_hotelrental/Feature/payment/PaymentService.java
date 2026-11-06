package sv7.setec.api_hotelrental.Feature.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sv7.setec.api_hotelrental.Feature.booking_audits.BookingAuditRepository;
import sv7.setec.api_hotelrental.Feature.booking_audits.models.BookingAuditLog;
import sv7.setec.api_hotelrental.Feature.bookings.BookingRepository;
import sv7.setec.api_hotelrental.Feature.bookings.Dtos.BookingResponseDto;
import sv7.setec.api_hotelrental.Feature.bookings.models.Booking;
import sv7.setec.api_hotelrental.Feature.enums.BookingStatus;
import sv7.setec.api_hotelrental.Feature.enums.PaymentMethod;
import sv7.setec.api_hotelrental.Feature.enums.PaymentStatus;
import sv7.setec.api_hotelrental.Feature.enums.RoomTypeStatus;
import sv7.setec.api_hotelrental.Feature.hotels.HotelRepository;
import sv7.setec.api_hotelrental.Feature.hotels.models.Hotel;
import sv7.setec.api_hotelrental.Feature.payment.Dtos.*;
import sv7.setec.api_hotelrental.Feature.payment.models.Payment;
import sv7.setec.api_hotelrental.Feature.rooms.RoomRepository;
import sv7.setec.api_hotelrental.Feature.roomtype.RoomtypeRepository;
import sv7.setec.api_hotelrental.Feature.roomtype.models.RoomType;
import sv7.setec.api_hotelrental.Feature.user.UserRepository;
import sv7.setec.api_hotelrental.Feature.user.models.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private static final EnumSet<BookingStatus> RESERVED_STATUSES =
            EnumSet.of(BookingStatus.PENDING, BookingStatus.APPROVED);

    private final PaymentRepository paymentRepository;
    private final BakongService bakongService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RoomtypeRepository roomtypeRepository;
    private final RoomRepository roomRepository;
    private final BookingAuditRepository bookingAuditRepository;

    /**
     * Generate dynamic KHQR code for customer to scan and pay.
     */
    public BakongQrResponseDto generateQr(GenerateQrRequestDto request) {
        return bakongService.generateDynamicKhqr(
                request.getAmount(),
                request.getCurrency(),
                request.getBillNumber()
        );
    }

    /**
     * Check transaction status with Bakong API by MD5.
     */
    public BakongCheckTransactionDto checkTransaction(String md5) {
        return bakongService.checkTransactionByMd5(md5);
    }

    /**
     * Process order with payment requirement:
     * - If KHQR: User must pay first. Checks Bakong by MD5.
     *   Only when status is PAID will the booking and payment be pushed to DB.
     * - If CASH: Pushes to DB with status PENDING.
     */
    @Transactional
    public PaymentOrderResponseDto processOrderPayment(PaymentOrderRequestDto request) {
        // 1. Validate dates
        validateDates(request.getCheckInDate(), request.getCheckOutDate());

        // 2. Verify customer exists
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with ID: " + request.getCustomerId()));

        // 3. Verify hotel exists
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found with ID: " + request.getHotelId()));

        // 4. Verify room type exists and belongs to hotel
        RoomType roomType = roomtypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room type not found with ID: " + request.getRoomTypeId()));

        if (!roomType.getHotel().getId().equals(hotel.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room type does not belong to selected hotel");
        }

        if (roomType.getStatus() != RoomTypeStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This room type is currently " + roomType.getStatus() + " and cannot be booked");
        }

        // 5. Verify room availability
        long totalRooms = roomRepository.countByHotelIdAndRoomTypeId(hotel.getId().longValue(), roomType.getId());
        long reservedRooms = bookingRepository.countOverlappingBookings(
                hotel.getId(),
                roomType.getId(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                RESERVED_STATUSES
        );

        if (totalRooms == 0 || reservedRooms >= totalRooms) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No room available for selected dates (Total: " + totalRooms + ", Booked: " + reservedRooms + ")");
        }

        // 6. Calculate total price and deposit
        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal totalPrice = roomType.getBasePrice()
                .multiply(BigDecimal.valueOf(nights))
                .setScale(2, RoundingMode.HALF_UP);

        int depositPct = hotel.getDepositPercentage() != null ? hotel.getDepositPercentage() : 0;
        BigDecimal depositAmount = totalPrice
                .multiply(BigDecimal.valueOf(depositPct))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // 7. Payment Verification Rule:
        BookingStatus bookingStatus;
        PaymentStatus paymentStatus;
        String message;

        if (request.getPaymentMethod() == PaymentMethod.KHQR) {
            if (request.getMd5() == null || request.getMd5().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment is required. MD5 transaction hash must be provided for KHQR payment.");
            }

            // Verify with NBC Bakong Open API
            BakongCheckTransactionDto checkResult = bakongService.checkTransactionByMd5(request.getMd5());
            if (!checkResult.isPaid()) {
                // DO NOT PUSH TO DB!
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Payment is required. KHQR transaction is unpaid or not found on Bakong (response: " + checkResult.getResponseMessage() + ")"
                );
            }

            bookingStatus = BookingStatus.APPROVED;
            paymentStatus = PaymentStatus.SUCCESS;
            message = "Order placed and paid successfully via Bakong KHQR";

        } else if (request.getPaymentMethod() == PaymentMethod.CASH) {
            bookingStatus = BookingStatus.PENDING;
            paymentStatus = PaymentStatus.PENDING;
            message = "Order placed with pending cash payment upon check-in";

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported payment method: " + request.getPaymentMethod());
        }

        // 8. Save Booking to Database
        Booking booking = Booking.builder()
                .customerId(request.getCustomerId())
                .hotelId(request.getHotelId())
                .roomTypeId(request.getRoomTypeId())
                .assignedRoomId(null)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalPrice(totalPrice)
                .depositAmount(depositAmount)
                .status(bookingStatus)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // 9. Save Payment to Database
        Payment payment = Payment.builder()
                .bookingId(savedBooking.getId())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(paymentStatus)
                .amount(request.getAmount())
                .md5(request.getMd5())
                .transactionHash(request.getMd5())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // 10. Audit Log
        bookingAuditRepository.save(BookingAuditLog.builder()
                .booking(savedBooking)
                .actor(customer)
                .action(paymentStatus == PaymentStatus.SUCCESS ? "PAID_AND_CREATED" : "CREATED_PENDING")
                .reason(message)
                .build());

        return PaymentOrderResponseDto.builder()
                .message(message)
                .booking(toBookingResponseDto(savedBooking))
                .payment(toPaymentResponseDto(savedPayment))
                .build();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getAllPayments() {
        return paymentRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::toPaymentResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found with ID: " + id));
        return toPaymentResponseDto(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .stream()
                .map(this::toPaymentResponseDto)
                .collect(Collectors.toList());
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (!checkIn.isBefore(checkOut)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkInDate must be strictly before checkOutDate");
        }
    }

    private PaymentResponseDto toPaymentResponseDto(Payment p) {
        return new PaymentResponseDto(
                p.getId(),
                p.getBookingId(),
                p.getPaymentMethod() != null ? p.getPaymentMethod().name() : null,
                p.getPaymentStatus() != null ? p.getPaymentStatus().name() : null,
                p.getAmount(),
                p.getCreatedAt()
        );
    }

    private BookingResponseDto toBookingResponseDto(Booking b) {
        return BookingResponseDto.builder()
                .id(b.getId())
                .customerId(b.getCustomerId())
                .hotelId(b.getHotelId())
                .roomTypeId(b.getRoomTypeId())
                .assignedRoomId(b.getAssignedRoomId())
                .checkInDate(b.getCheckInDate())
                .checkOutDate(b.getCheckOutDate())
                .totalPrice(b.getTotalPrice())
                .depositAmount(b.getDepositAmount())
                .status(b.getStatus())
                .createdAt(b.getCreatedAt())
                .build();
    }
}