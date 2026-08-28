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
import sv7.setec.api_hotelrental.Feature.hotels.HotelRepository;
import sv7.setec.api_hotelrental.Feature.hotels.models.Hotel;
import sv7.setec.api_hotelrental.Feature.rooms.RoomRepository;
import sv7.setec.api_hotelrental.Feature.rooms.models.Room;
import sv7.setec.api_hotelrental.Feature.roomtype.RoomtypeRepository;
import sv7.setec.api_hotelrental.Feature.roomtype.models.RoomType;
import sv7.setec.api_hotelrental.Feature.user.UserRepository;

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

    /*
     * Create booking
     */
    @Transactional
    public BookingResponseDto createBooking(
            BookingRequestDto request
    ) {
        validateDates(
                request.getCheckInDate(),
                request.getCheckOutDate()
        );

        // Check customer
        if (!userRepository.existsById(request.getCustomerId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Customer not found"
            );
        }

        // Check hotel
        Hotel hotel = hotelRepository
                .findById(request.getHotelId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Hotel not found"
                        )
                );

        // Check room type
        RoomType roomType = roomtypeRepository
                .findById(request.getRoomTypeId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Room type not found"
                        )
                );

        // Room type must belong to selected hotel
        if (!roomType.getHotel()
                .getId()
                .equals(hotel.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Room type does not belong to selected hotel"
            );
        }

        // Count all physical rooms of selected type
        long totalRooms =
                roomRepository
                        .countByHotelIdAndRoomTypeIdAndStatus(
                                hotel.getId().longValue(),
                                roomType.getId(),
                                RoomStatus.AVAILABLE
                        );

        // Count rooms already reserved in date range
        long reservedRooms =
                bookingRepository.countOverlappingBookings(
                        hotel.getId(),
                        roomType.getId(),
                        request.getCheckInDate(),
                        request.getCheckOutDate(),
                        RESERVED_STATUSES
                );

        if (totalRooms == 0 || reservedRooms >= totalRooms) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No room available for selected dates"
            );
        }

        // Calculate number of nights
        long nights = ChronoUnit.DAYS.between(
                request.getCheckInDate(),
                request.getCheckOutDate()
        );

        // totalPrice = basePrice × nights
        BigDecimal totalPrice =
                roomType.getBasePrice()
                        .multiply(BigDecimal.valueOf(nights))
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        // deposit = totalPrice × percentage / 100
        BigDecimal depositAmount =
                totalPrice
                        .multiply(
                                BigDecimal.valueOf(
                                        hotel.getDepositPercentage()
                                )
                        )
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

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

        Booking savedBooking =
                bookingRepository.save(booking);

        return toResponseDto(savedBooking);
    }

    /*
     * Get all bookings
     */
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookings() {
        return bookingRepository
                .findAll(
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                )
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
    public List<BookingResponseDto> getByCustomer(
            Long customerId
    ) {
        return bookingRepository
                .findByCustomerIdOrderByCreatedAtDesc(
                        customerId
                )
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    /*
     * Hotel bookings
     */
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getByHotel(
            Integer hotelId
    ) {
        return bookingRepository
                .findByHotelIdOrderByCreatedAtDesc(
                        hotelId
                )
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
        validateDates(
                checkInDate,
                checkOutDate
        );

        long totalRooms =
                roomRepository
                        .countByHotelIdAndRoomTypeIdAndStatus(
                                hotelId.longValue(),
                                roomTypeId,
                                RoomStatus.AVAILABLE
                        );

        long reservedRooms =
                bookingRepository.countOverlappingBookings(
                        hotelId,
                        roomTypeId,
                        checkInDate,
                        checkOutDate,
                        RESERVED_STATUSES
                );

        return Math.max(
                0,
                totalRooms - reservedRooms
        );
    }

    /*
     * Approve booking and assign physical room
     */
    @Transactional
    public BookingResponseDto approveBooking(
            Long bookingId,
            Long roomId
    ) {
        Booking booking = findBooking(bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only PENDING booking can be approved"
            );
        }

        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Room not found"
                        )
                );

        // Check room belongs to same hotel
        if (!room.getHotelId().equals(
                booking.getHotelId().longValue()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Room does not belong to booking hotel"
            );
        }

        // Check room type
        if (!room.getRoomType()
                .getId()
                .equals(booking.getRoomTypeId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Room does not match booking room type"
            );
        }

        // Room under maintenance cannot be assigned
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Room is not available"
            );
        }

        // Check same physical room date conflict
        long conflicts =
                bookingRepository
                        .countAssignedRoomConflicts(
                                roomId,
                                bookingId,
                                booking.getCheckInDate(),
                                booking.getCheckOutDate()
                        );

        if (conflicts > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Room already assigned for selected dates"
            );
        }

        booking.setAssignedRoomId(roomId);
        booking.setStatus(BookingStatus.APPROVED);

        return toResponseDto(
                bookingRepository.save(booking)
        );
    }

    /*
     * Change booking status
     */
    @Transactional
    public BookingResponseDto updateStatus(
            Long bookingId,
            BookingStatus newStatus
    ) {
        Booking booking = findBooking(bookingId);

        BookingStatus currentStatus =
                booking.getStatus();

        boolean validTransition =
                switch (currentStatus) {

                    case PENDING ->
                            newStatus == BookingStatus.REJECTED
                            || newStatus == BookingStatus.CANCELLED;

                    case APPROVED ->
                            newStatus == BookingStatus.FINISHED
                            || newStatus == BookingStatus.CANCELLED;

                    case REJECTED, FINISHED, CANCELLED ->
                            false;

                    default ->
                            false;
                };

        if (!validTransition) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot change booking from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }

        booking.setStatus(newStatus);

        return toResponseDto(
                bookingRepository.save(booking)
        );
    }

    /*
     * Find booking or return 404
     */
    private Booking findBooking(Long id) {
        return bookingRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Booking not found"
                        )
                );
    }

    /*
     * Check date
     */
    private void validateDates(
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {
        if (checkInDate == null || checkOutDate == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Check-in and check-out dates are required"
            );
        }

        if (!checkOutDate.isAfter(checkInDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Check-out date must be after check-in date"
            );
        }
    }

    /*
     * Entity to Response DTO
     */
    private BookingResponseDto toResponseDto(
            Booking booking
    ) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .customerId(booking.getCustomerId())
                .hotelId(booking.getHotelId())
                .roomTypeId(booking.getRoomTypeId())
                .assignedRoomId(
                        booking.getAssignedRoomId()
                )
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalPrice(booking.getTotalPrice())
                .depositAmount(
                        booking.getDepositAmount()
                )
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}