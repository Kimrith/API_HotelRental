package sv7.setec.api_hotelrental.Feature.booking_audits.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import sv7.setec.api_hotelrental.Feature.bookings.models.Booking;
import sv7.setec.api_hotelrental.Feature.user.models.User;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_user_id", nullable = false)
    private User actor;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
