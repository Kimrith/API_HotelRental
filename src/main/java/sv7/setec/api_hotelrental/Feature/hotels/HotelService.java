package sv7.setec.api_hotelrental.Feature.hotels;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv7.setec.api_hotelrental.Feature.hotels.Dtos.HotelRequestDto;
import sv7.setec.api_hotelrental.Feature.hotels.Dtos.HotelResponseDto;
import sv7.setec.api_hotelrental.Feature.hotels.models.Hotel;
import sv7.setec.api_hotelrental.Feature.hotels.models.HotelImage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    public List<HotelResponseDto> getAllHotels() {
        return hotelRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public HotelResponseDto getHotelById(Integer id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));
        return mapToResponseDto(hotel);
    }

    @Transactional
    public HotelResponseDto createHotel(HotelRequestDto requestDto) {
        Hotel hotel = Hotel.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .address(requestDto.getAddress())
                .city(requestDto.getCity())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .depositPercentage(requestDto.getDepositPercentage() != null ? requestDto.getDepositPercentage() : 20)
                .status(requestDto.getStatus() != null ? requestDto.getStatus() : "PENDING")
                .build();

        if (requestDto.getImages() != null && !requestDto.getImages().isEmpty()) {
            List<HotelImage> hotelImages = requestDto.getImages().stream()
                    .map(imgDto -> HotelImage.builder()
                            .hotel(hotel)
                            .imageUrl(imgDto.getImageUrl())
                            .isBanner(imgDto.getIsBanner() != null ? imgDto.getIsBanner() : false)
                            .orderIndex(imgDto.getOrderIndex() != null ? imgDto.getOrderIndex() : 0)
                            .build())
                    .collect(Collectors.toList());
            hotel.setImages(hotelImages);
        }

        Hotel savedHotel = hotelRepository.save(hotel);
        return mapToResponseDto(savedHotel);
    }

    @Transactional
    public void deleteHotel(Integer id) {
        if (!hotelRepository.existsById(id)) {
            throw new RuntimeException("Hotel not found with id: " + id);
        }
        hotelRepository.deleteById(id);
    }

    private HotelResponseDto mapToResponseDto(Hotel hotel) {
        List<HotelResponseDto.ImageResponseDto> imageDtos = (hotel.getImages() == null)
                ? Collections.emptyList()
                : hotel.getImages().stream()
                .map(img -> HotelResponseDto.ImageResponseDto.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .isBanner(img.getIsBanner())
                        .orderIndex(img.getOrderIndex())
                        .build())
                .collect(Collectors.toList());

        return HotelResponseDto.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .address(hotel.getAddress())
                .city(hotel.getCity())
                .latitude(hotel.getLatitude())
                .longitude(hotel.getLongitude())
                .depositPercentage(hotel.getDepositPercentage())
                .status(hotel.getStatus())
                .createdAt(hotel.getCreatedAt())
                .images(imageDtos)
                .build();
    }
}