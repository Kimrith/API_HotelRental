package sv7.setec.api_hotelrental.Feature.bookings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sv7.setec.api_hotelrental.Feature.bookings.models.Booking;
import sv7.setec.api_hotelrental.Feature.enums.BookingStatus;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerIdOrderByCreatedAtDesc(
            Long customerId
    );

    List<Booking> findByHotelIdOrderByCreatedAtDesc(
            Integer hotelId
    );

    List<Booking> findByStatusOrderByCreatedAtDesc(
            BookingStatus status
    );

    @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.hotelId = :hotelId
          AND b.roomTypeId = :roomTypeId
          AND b.status IN :statuses
          AND b.checkInDate < :checkOutDate
          AND b.checkOutDate > :checkInDate
    """)
    long countOverlappingBookings(
            @Param("hotelId") Integer hotelId,
            @Param("roomTypeId") Long roomTypeId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("statuses")
            Collection<BookingStatus> statuses
    );

    @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.assignedRoomId = :roomId
          AND b.id <> :bookingId
          AND b.status = 'APPROVED'
          AND b.checkInDate < :checkOutDate
          AND b.checkOutDate > :checkInDate
    """)
    long countAssignedRoomConflicts(
            @Param("roomId") Long roomId,
            @Param("bookingId") Long bookingId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );
}