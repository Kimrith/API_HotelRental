package sv7.setec.api_hotelrental.Feature.roomtype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv7.setec.api_hotelrental.Feature.enums.RoomStatus;
import sv7.setec.api_hotelrental.Feature.roomtype.models.RoomType;

import java.util.List;

@Repository
public interface RoomtypeRepository extends JpaRepository<RoomType, Long> {

    // Traverses the hotel relationship to match Hotel.id
    List<RoomType> findByHotel_Id(Integer hotelId);

    List<RoomType> findByNameContainingIgnoreCase(String name);

    // Find room types by status
    List<RoomType> findByStatus(RoomStatus status);

    // Find room types belonging to a specific hotel and having a specific status
    List<RoomType> findByHotel_IdAndStatus(Integer hotelId, RoomStatus status);

    // Check availability of a specific room type by its ID and status
    boolean existsByIdAndStatus(Long id, RoomStatus status);
}