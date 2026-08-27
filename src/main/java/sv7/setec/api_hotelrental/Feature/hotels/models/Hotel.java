package sv7.setec.api_hotelrental.Feature.hotels.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import sv7.setec.api_hotelrental.Feature.user.models.User; // Adjust package path if needed

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Builder.Default
    @Column(name = "deposit_percentage", nullable = false)
    private Integer depositPercentage = 20;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    // --- OWNER RELATIONSHIP (Fixes the missing column / symbol error) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HotelImage> images = new ArrayList<>();
}