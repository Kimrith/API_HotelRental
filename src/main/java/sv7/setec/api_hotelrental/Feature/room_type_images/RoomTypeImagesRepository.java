package sv7.setec.api_hotelrental.Feature.room_type_images;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv7.setec.api_hotelrental.Feature.room_type_images.models.RoomTypeImages;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomTypeImagesRepository extends JpaRepository<RoomTypeImages, Long> {

    List<RoomTypeImages> findByRoomTypeId(Long roomTypeId);

    Optional<RoomTypeImages> findByRoomTypeIdAndIsPrimaryTrue(Long roomTypeId);

    void deleteByRoomTypeId(Long roomTypeId);
}