package sv7.setec.api_hotelrental.Feature.rooms;

import sv7.setec.api_hotelrental.Feature.rooms.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    List<Room> findByHotelId(Long hotelId);
    Optional<Room> findByIdAndIsDeletedFalse(Long id);
    long countByHotelIdAndRoomTypeId(Long hotelId, Long roomTypeId);
}