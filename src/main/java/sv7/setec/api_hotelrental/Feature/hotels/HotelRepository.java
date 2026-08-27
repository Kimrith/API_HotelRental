package sv7.setec.api_hotelrental.Feature.hotels;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv7.setec.api_hotelrental.Feature.hotels.models.Hotel;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {

    List<Hotel> findByCityIgnoreCase(String city);

    List<Hotel> findByStatus(String status);

    // Fetch all hotels belonging to a specific owner
    List<Hotel> findByOwnerId(Integer ownerId);

    // Check if an owner already owns a hotel with a given name
    boolean existsByNameIgnoreCaseAndOwnerId(String name, Integer ownerId);
}