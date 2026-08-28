package sv7.setec.api_hotelrental.Feature.bookings;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sv7.setec.api_hotelrental.Feature.bookings.Dtos.BookingRequestDto;
import sv7.setec.api_hotelrental.Feature.bookings.Dtos.BookingResponseDto;
import sv7.setec.api_hotelrental.Feature.bookings.models.Booking;
import sv7.setec.api_hotelrental.Feature.enums.BookingStatus;
import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;
import sv7.setec.api_hotelrental.Feature.enums.RoomTypeStatus;
import sv7.setec.api_hotelrental.Feature.hotels.HotelRepository;
import sv7.setec.api_hotelrental.Feature.hotels.models.Hotel;
import sv7.setec.api_hotelrental.Feature.rooms.RoomRepository;
import sv7.setec.api_hotelrental.Feature.rooms.models.Room;
import sv7.setec.api_hotelrental.Feature.roomtype.RoomtypeRepository;
import sv7.setec.api_hotelrental.Feature.roomtype.models.RoomType;
import sv7.setec.api_hotelrental.Feature.user.UserRepository;
import sv7.setec.api_hotelrental.Feature.user.models.User;
import sv7.setec.api_hotelrental.Feature.booking_audits.BookingAuditRepository;
import sv7.setec.api_hotelrental.Feature.booking_audits.models.BookingAuditLog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final EnumSet<BookingStatus> RESERVED_STATUSES =
            EnumSet.of(
                    BookingStatus.PENDING,
                    BookingStatus.APPROVED
            );

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RoomtypeRepository roomtypeRepository;
    private final RoomRepository roomRepository;
    private final BookingAuditRepository bookingAuditRepository;

    /*
     * Create booking
     */
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto request) {
        validateDates(request.getCheckInDate(), request.getCheckOutDate());

        // 1. Verify customer exists
        if (!userRepository.existsById(request.getCustomerId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Customer not found with ID: " + request.getCustomerId()
            );
        }

        // 2. Verify hotel exists
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Hotel not found with ID: " + request.getHotelId()
                ));

        // 3. Verify room type exists
        RoomType roomType = roomtypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Room type not found with ID: " + request.getRoomTypeId()
                ));

        // 4. Room type must belong to selected hotel
        if (!roomType.getHotel().getId().equals(hotel.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Room type does not belong to the selected hotel"
            );
        }

        // 5. Room type must be AVAILABLE (not MAINTENANCE or UNAVAILABLE)
        if (roomType.getStatus() != RoomTypeStatus.AVAILABLE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This room type is currently " + roomType.getStatus() + " and cannot be booked"
            );
        }

        // 6. Count total physical rooms of selected type
        long totalRooms = roomRepository.countByHotelIdAndRoomTypeId(
                hotel.getId().longValue(),
                roomType.getId()
        );

        // 7. Count active overlapping bookings in date range
        long reservedRooms = bookingRepository.countOverlappingBookings(
                hotel.getId(),
                roomType.getId(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                RESERVED_STATUSES
        );

        if (totalRooms == 0 || reservedRooms >= totalRooms) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No room available for selected dates (Total: " + totalRooms + ", Booked: " + reservedRooms + ")"
            );
        }

        // 8. Calculate total price and deposit
        long nights = ChronoUnit.DAYS.between(
                request.getCheckInDate(),
                request.getCheckOutDate()
        );

        BigDecimal totalPrice = roomType.getBasePrice()
                .multiply(BigDecimal.valueOf(nights))
                .setScale(2, RoundingMode.HALF_UP);

        int depositPct = hotel.getDepositPercentage() != null ? hotel.getDepositPercentage() : 0;
        BigDecimal depositAmount = totalPrice
                .multiply(BigDecimal.valueOf(depositPct))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        Booking booking = Booking.builder()
                .customerId(request.getCustomerId())
                .hotelId(request.getHotelId())
                .roomTypeId(request.getRoomTypeId())
                .assignedRoomId(null)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalPrice(totalPrice)
                .depositAmount(depositAmount)
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        bookingAuditRepository.save(BookingAuditLog.builder()
                .booking(savedBooking)
                .actor(getCurrentActor(request.getCustomerId()))
                .action("CREATED")
                .reason("Booking initialized by customer")
                .build());
        return toResponseDto(savedBooking);
    }

    /*
     * Get all bookings
     */
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookings() {
        return bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    /*
     * Get booking by ID
     */
    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(Long id) {
        return toResponseDto(findBooking(id));
    }

    /*
     * Customer booking history
     */
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getByCustomer(Long customerId) {
        return bookingRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    /*
     * Hotel bookings
     */
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getByHotel(Integer hotelId) {
        return bookingRepository.findByHotelIdOrderByCreatedAtDesc(hotelId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    /*
     * Check available room count
     */
    @Transactional(readOnly = true)
    public long getAvailableRoomCount(
            Integer hotelId,
            Long roomTypeId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {
        validateDates(checkInDate, checkOutDate);

        RoomType roomType = roomtypeRepository.findById(roomTypeId).orElse(null);
        if (roomType == null || roomType.getStatus() != RoomTypeStatus.AVAILABLE) {
            return 0;
        }

        long totalRooms = roomRepository.countByHotelIdAndRoomTypeId(
                hotelId.longValue(),
                roomTypeId
        );

        long reservedRooms = bookingRepository.countOverlappingBookings(
                hotelId,
                roomTypeId,
                checkInDate,
                checkOutDate,
                RESERVED_STATUSES
        );

        return Math.max(0, totalRooms - reservedRooms);
    }

    /*
     * Approve booking and assign physical room
     */
    @Transactional
    public BookingResponseDto approveBooking(Long bookingId, Long roomId) {
        Booking booking = findBooking(bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only PENDING bookings can be approved"
            );
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Room not found with ID: " + roomId
                ));

        // Check room belongs to the same hotel
        if (!room.getHotelId().equals(booking.getHotelId().longValue())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Room does not belong to the booking hotel"
            );
        }

        // Check room type matches
        if (!room.getRoomType().getId().equals(booking.getRoomTypeId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Room does not match the booked room type"
            );
        }

        // Check if this specific physical room is already assigned to another active booking during these dates
        long conflicts = bookingRepository.countAssignedRoomConflicts(
                roomId,
                bookingId,
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );

        if (conflicts > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Physical room is already assigned to another booking for selected dates"
            );
        }

        booking.setAssignedRoomId(roomId);
        booking.setStatus(BookingStatus.APPROVED);

        Booking savedBooking = bookingRepository.save(booking);
        bookingAuditRepository.save(BookingAuditLog.builder()
                .booking(savedBooking)
                .actor(getCurrentActor(savedBooking.getCustomerId()))
                .action("STATUS_CHANGED")
                .reason("Booking approved and room " + roomId + " assigned")
                .build());
        return toResponseDto(savedBooking);
    }

    /*
     * Change booking status (State Machine Transition)
     */
    @Transactional
    public BookingResponseDto updateStatus(Long bookingId, BookingStatus newStatus) {
        Booking booking = findBooking(bookingId);
        BookingStatus currentStatus = booking.getStatus();

        boolean validTransition = switch (currentStatus) {
            case PENDING -> newStatus == BookingStatus.REJECTED || newStatus == BookingStatus.CANCELLED;
            case APPROVED -> newStatus == BookingStatus.FINISHED || newStatus == BookingStatus.CANCELLED;
            case REJECTED, FINISHED, CANCELLED -> false;
            default -> false;
        };

        if (!validTransition) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot transition booking status from " + currentStatus + " to " + newStatus
            );
        }

        booking.setStatus(newStatus);
        Booking savedBooking = bookingRepository.save(booking);
        bookingAuditRepository.save(BookingAuditLog.builder()
                .booking(savedBooking)
                .actor(getCurrentActor(savedBooking.getCustomerId()))
                .action("STATUS_CHANGED")
                .reason("Booking status changed to " + newStatus)
                .build());
        return toResponseDto(savedBooking);
    }

    private User getCurrentActor(Long fallbackUserId) {
        org.springframework.security.core.Authentication auth = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return userRepository.findByUsername(auth.getName())
                    .orElseGet(() -> userRepository.findByEmail(auth.getName())
                            .orElseGet(() -> userRepository.findById(fallbackUserId)
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Actor user not found"))));
        }
        return userRepository.findById(fallbackUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Actor user not found"));
    }

    /*
     * Find booking or return 404
     */
    private Booking findBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found with ID: " + id
                ));
    }

    /*
     * Validate date boundaries
     */
    private void validateDates(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Check-in and check-out dates are required"
            );
        }

        if (!checkOutDate.isAfter(checkInDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Check-out date must be strictly after check-in date"
            );
        }
    }

    /*
     * Entity to Response DTO
     */
    private BookingResponseDto toResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .customerId(booking.getCustomerId())
                .hotelId(booking.getHotelId())
                .roomTypeId(booking.getRoomTypeId())
                .assignedRoomId(booking.getAssignedRoomId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalPrice(booking.getTotalPrice())
                .depositAmount(booking.getDepositAmount())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}