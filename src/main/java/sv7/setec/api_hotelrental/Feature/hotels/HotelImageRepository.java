package sv7.setec.api_hotelrental.Feature.hotels;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv7.setec.api_hotelrental.Feature.hotels.models.HotelImage;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelImageRepository extends JpaRepository<HotelImage, Integer> {

    List<HotelImage> findByHotelIdOrderByOrderIndexAsc(Integer hotelId);

    Optional<HotelImage> findByHotelIdAndIsBannerTrue(Integer hotelId);

    // Add this line to resolve the compilation error
    int countByHotelId(Integer hotelId);
}