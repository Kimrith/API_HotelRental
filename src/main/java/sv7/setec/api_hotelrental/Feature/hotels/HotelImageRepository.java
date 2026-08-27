package sv7.setec.api_hotelrental.Feature.hotels;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv7.setec.api_hotelrental.Feature.hotels.models.HotelImage;

import java.util.List;

@Repository
public interface HotelImageRepository extends JpaRepository<HotelImage, Integer> {
    List<HotelImage> findByHotelIdOrderByOrderIndexAsc(Integer hotelId);
    void deleteByHotelId(Integer hotelId);
}