package sv7.setec.api_hotelrental.Feature.hotels;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv7.setec.api_hotelrental.Feature.hotels.Dtos.HotelImageRequestDto;
import sv7.setec.api_hotelrental.Feature.hotels.Dtos.HotelImageResponseDto;
import sv7.setec.api_hotelrental.Feature.hotels.models.Hotel;
import sv7.setec.api_hotelrental.Feature.hotels.models.HotelImage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelImageService {

    private final HotelImageRepository hotelImageRepository;
    private final HotelRepository hotelRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public List<HotelImageResponseDto> getImagesByHotelId(Integer hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new RuntimeException("Hotel not found with id: " + hotelId);
        }

        return hotelImageRepository.findByHotelIdOrderByOrderIndexAsc(hotelId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public HotelImageResponseDto addImageToHotel(HotelImageRequestDto requestDto) {
        Hotel hotel = hotelRepository.findById(requestDto.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + requestDto.getHotelId()));

        // Save physical file via your storage service
        String storedPath = fileStorageService.storeFile(requestDto.getFile());
        boolean isBanner = Boolean.TRUE.equals(requestDto.getIsBanner());

        // If newly uploaded image is marked as banner, unset any existing banner
        if (isBanner) {
            hotelImageRepository.findByHotelIdAndIsBannerTrue(hotel.getId())
                    .ifPresent(existingBanner -> {
                        existingBanner.setIsBanner(false);
                        hotelImageRepository.save(existingBanner);
                    });
        }

        int orderIndex = (requestDto.getOrderIndex() != null)
                ? requestDto.getOrderIndex()
                : hotelImageRepository.countByHotelId(hotel.getId());

        HotelImage hotelImage = HotelImage.builder()
                .hotel(hotel)
                .imageUrl(storedPath)
                .isBanner(isBanner)
                .orderIndex(orderIndex)
                .build();

        HotelImage savedImage = hotelImageRepository.save(hotelImage);
        return mapToResponseDto(savedImage);
    }

    @Transactional
    public void deleteImage(Integer imageId) {
        if (!hotelImageRepository.existsById(imageId)) {
            throw new RuntimeException("Image not found with id: " + imageId);
        }
        hotelImageRepository.deleteById(imageId);
    }

    private HotelImageResponseDto mapToResponseDto(HotelImage image) {
        return HotelImageResponseDto.builder()
                .id(image.getId())
                .hotelId(image.getHotel() != null ? image.getHotel().getId() : null)
                .imageUrl(image.getImageUrl())
                .isBanner(image.getIsBanner())
                .orderIndex(image.getOrderIndex())
                .build();
    }
}