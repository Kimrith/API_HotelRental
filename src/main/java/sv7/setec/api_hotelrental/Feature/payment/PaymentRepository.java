package sv7.setec.api_hotelrental.Feature.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv7.setec.api_hotelrental.Feature.payment.models.Payment;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingId(Long bookingId);
    Optional<Payment> findFirstByBookingIdOrderByCreatedAtDesc(Long bookingId);
}