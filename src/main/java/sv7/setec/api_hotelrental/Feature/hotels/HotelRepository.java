package sv7.setec.api_hotelrental.Feature.hotels;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv7.setec.api_hotelrental.Feature.hotels.models.Hotel;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    List<Hotel> findByCityIgnoreCase(String city);
    List<Hotel> findByStatus(String status);
}