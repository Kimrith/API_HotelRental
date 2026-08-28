package sv7.setec.api_hotelrental.Feature.roomtype.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;
import sv7.setec.api_hotelrental.Feature.enums.RoomTypeStatus;
import sv7.setec.api_hotelrental.Feature.hotels.models.Hotel;

import java.math.BigDecimal;

@Entity
@Table(name = "room_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonBackReference
    private Hotel hotel;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "base_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal basePrice;

    @Column(name = "max_guests", nullable = false)
    private Integer maxGuests;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private RoomTypeStatus status = RoomTypeStatus.AVAILABLE;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "search_keywords", columnDefinition = "TEXT")
    private String searchKeywords;
}