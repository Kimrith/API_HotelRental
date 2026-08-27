package sv7.setec.api_hotelrental.Feature.room_type_images;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sv7.setec.api_hotelrental.Feature.hotels.FileStorageService;
import sv7.setec.api_hotelrental.Feature.room_type_images.Dtos.RoomTypeImagesRequestDto;
import sv7.setec.api_hotelrental.Feature.room_type_images.Dtos.RoomTypeImagesResponseDto;
import sv7.setec.api_hotelrental.Feature.room_type_images.models.RoomTypeImages;
import sv7.setec.api_hotelrental.Feature.roomtype.RoomtypeRepository;
import sv7.setec.api_hotelrental.Feature.roomtype.models.RoomType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomTypeImagesServiceImpl implements RoomTypeImagesService {

    private final RoomTypeImagesRepository roomTypeImagesRepository;
    private final RoomtypeRepository roomTypeRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public List<RoomTypeImagesResponseDto> uploadImages(Long roomTypeId, List<MultipartFile> files, Integer primaryIndex) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("RoomType not found with ID: " + roomTypeId));

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("No files provided for upload");
        }

        // If setting a primary image in this batch, reset existing primary image
        if (primaryIndex != null && primaryIndex >= 0 && primaryIndex < files.size()) {
            roomTypeImagesRepository.findByRoomTypeIdAndIsPrimaryTrue(roomTypeId)
                    .ifPresent(existingPrimary -> {
                        existingPrimary.setIsPrimary(false);
                        roomTypeImagesRepository.save(existingPrimary);
                    });
        }

        List<RoomTypeImages> savedImages = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (file != null && !file.isEmpty()) {
                String storedPath = fileStorageService.storeFile(file);
                boolean isPrimary = (primaryIndex != null && primaryIndex == i);

                RoomTypeImages imageEntity = RoomTypeImages.builder()
                        .roomType(roomType)
                        .imageUrl(storedPath)
                        .isPrimary(isPrimary)
                        .build();

                savedImages.add(roomTypeImagesRepository.save(imageEntity));
            }
        }

        return savedImages.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoomTypeImagesResponseDto addImageUrl(RoomTypeImagesRequestDto requestDto) {
        RoomType roomType = roomTypeRepository.findById(requestDto.getRoomTypeId())
                .orElseThrow(() -> new RuntimeException("RoomType not found with ID: " + requestDto.getRoomTypeId()));

        boolean isPrimary = Boolean.TRUE.equals(requestDto.getIsPrimary());

        if (isPrimary) {
            roomTypeImagesRepository.findByRoomTypeIdAndIsPrimaryTrue(roomType.getId())
                    .ifPresent(existingPrimary -> {
                        existingPrimary.setIsPrimary(false);
                        roomTypeImagesRepository.save(existingPrimary);
                    });
        }

        String fileUrl = fileStorageService.storeFile(requestDto.getFile());

        RoomTypeImages imageEntity = RoomTypeImages.builder()
                .roomType(roomType)
                .imageUrl(fileUrl)
                .isPrimary(isPrimary)
                .build();

        RoomTypeImages saved = roomTypeImagesRepository.save(imageEntity);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeImagesResponseDto> getImagesByRoomTypeId(Long roomTypeId) {
        return roomTypeImagesRepository.findByRoomTypeId(roomTypeId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void setPrimaryImage(Long imageId) {
        RoomTypeImages targetImage = roomTypeImagesRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Room type image not found with ID: " + imageId));

        Long roomTypeId = targetImage.getRoomType().getId().longValue();

        // Unset any previous primary image
        roomTypeImagesRepository.findByRoomTypeIdAndIsPrimaryTrue(roomTypeId)
                .ifPresent(existingPrimary -> {
                    existingPrimary.setIsPrimary(false);
                    roomTypeImagesRepository.save(existingPrimary);
                });

        targetImage.setIsPrimary(true);
        roomTypeImagesRepository.save(targetImage);
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        RoomTypeImages targetImage = roomTypeImagesRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Room type image not found with ID: " + imageId));

        roomTypeImagesRepository.delete(targetImage);
    }

    private RoomTypeImagesResponseDto mapToResponseDto(RoomTypeImages entity) {
        return RoomTypeImagesResponseDto.builder()
                .id(entity.getId())
                .roomTypeId(entity.getRoomType() != null ? entity.getRoomType().getId().longValue() : null)
                .imageUrl(entity.getImageUrl())
                .isPrimary(entity.getIsPrimary())
                .build();
    }
}