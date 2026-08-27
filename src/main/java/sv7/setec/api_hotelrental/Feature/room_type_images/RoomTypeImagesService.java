package sv7.setec.api_hotelrental.Feature.room_type_images;

import org.springframework.web.multipart.MultipartFile;
import sv7.setec.api_hotelrental.Feature.room_type_images.Dtos.RoomTypeImagesRequestDto;
import sv7.setec.api_hotelrental.Feature.room_type_images.Dtos.RoomTypeImagesResponseDto;

import java.util.List;

public interface RoomTypeImagesService {

    // Upload multiple image files from local device
    List<RoomTypeImagesResponseDto> uploadImages(Long roomTypeId, List<MultipartFile> files, Integer primaryIndex);

    // Add image using direct URL
    RoomTypeImagesResponseDto addImageUrl(RoomTypeImagesRequestDto requestDto);

    // Retrieve all images belonging to a specific room type
    List<RoomTypeImagesResponseDto> getImagesByRoomTypeId(Long roomTypeId);

    // Set an image as the primary cover photo
    void setPrimaryImage(Long imageId);

    // Delete an image by ID
    void deleteImage(Long imageId);
}