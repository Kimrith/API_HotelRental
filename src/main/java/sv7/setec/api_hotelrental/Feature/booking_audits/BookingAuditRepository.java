package sv7.setec.api_hotelrental.Feature.booking_audits;

import sv7.setec.api_hotelrental.Feature.booking_audits.models.BookingAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingAuditRepository extends JpaRepository<BookingAuditLog, Long> {
    Page<BookingAuditLog> findByBookingId(Long bookingId, Pageable pageable);
}
