package sv7.setec.api_hotelrental.Feature.rooms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;
import sv7.setec.api_hotelrental.Feature.rooms.models.Room;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // Retrieve all rooms belonging to a specific hotel
    List<Room> findByHotelId(Long hotelId);

    // Retrieve rooms by room type
    List<Room> findByRoomTypeId(Long roomTypeId);

    // Retrieve rooms by hotel and status (e.g., all AVAILABLE rooms in hotel 1)
    List<Room> findByHotelIdAndStatus(Long hotelId, RoomStatus status);

    // Check if a room number already exists within a hotel
    boolean existsByHotelIdAndRoomNumber(Long hotelId, String roomNumber);

    // Find a specific room by hotel and room number
    Optional<Room> findByHotelIdAndRoomNumber(Long hotelId, String roomNumber);
}