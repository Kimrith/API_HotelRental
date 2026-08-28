package sv7.setec.api_hotelrental.Feature.bookings.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import sv7.setec.api_hotelrental.Feature.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;

    @Column(name = "room_type_id", nullable = false)
    private Long roomTypeId;

    // Null while booking is PENDING
    @Column(name = "assigned_room_id")
    private Long assignedRoomId;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(
        name = "total_price",
        precision = 10,
        scale = 2,
        nullable = false
    )
    private BigDecimal totalPrice;

    @Column(
        name = "deposit_amount",
        precision = 10,
        scale = 2,
        nullable = false
    )
    private BigDecimal depositAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @CreationTimestamp
    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    private LocalDateTime createdAt;
}