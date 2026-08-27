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

    public List<HotelImageResponseDto> getImagesByHotelId(Integer hotelId) {
        return hotelImageRepository.findByHotelIdOrderByOrderIndexAsc(hotelId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public HotelImageResponseDto addImageToHotel(HotelImageRequestDto requestDto) {
        Hotel hotel = hotelRepository.findById(requestDto.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + requestDto.getHotelId()));

        HotelImage hotelImage = HotelImage.builder()
                .hotel(hotel)
                .imageUrl(requestDto.getImageUrl())
                .isBanner(requestDto.getIsBanner() != null ? requestDto.getIsBanner() : false)
                .orderIndex(requestDto.getOrderIndex() != null ? requestDto.getOrderIndex() : 0)
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