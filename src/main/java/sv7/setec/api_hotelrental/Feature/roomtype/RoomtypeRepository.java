package sv7.setec.api_hotelrental.Feature.roomtype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv7.setec.api_hotelrental.Feature.roomtype.models.RoomType;

import java.util.List;

@Repository
public interface RoomtypeRepository extends JpaRepository<RoomType, Long> {

    // Traverses the hotel relationship to match Hotel.id
    List<RoomType> findByHotel_Id(Integer hotelId);

    List<RoomType> findByNameContainingIgnoreCase(String name);
}