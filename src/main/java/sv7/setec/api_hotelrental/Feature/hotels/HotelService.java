package sv7.setec.api_hotelrental.Feature.hotels;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv7.setec.api_hotelrental.Feature.hotels.Dtos.HotelRequestDto;
import sv7.setec.api_hotelrental.Feature.hotels.Dtos.HotelResponseDto;
import sv7.setec.api_hotelrental.Feature.hotels.models.Hotel;
import sv7.setec.api_hotelrental.Feature.user.UserRepository;
import sv7.setec.api_hotelrental.Feature.user.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<HotelResponseDto> getAllHotels() {
        return hotelRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HotelResponseDto getHotelById(Integer id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));
        return mapToResponseDto(hotel);
    }

    @Transactional
    public HotelResponseDto createHotel(HotelRequestDto requestDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("User must be authenticated to create a hotel");
        }

        User owner = userRepository.findByUsername(auth.getName())
                .orElseGet(() -> userRepository.findByEmail(auth.getName())
                        .orElseThrow(() -> new RuntimeException("Owner not found for: " + auth.getName())));

        Hotel hotel = Hotel.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .address(requestDto.getAddress())
                .city(requestDto.getCity())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .depositPercentage(requestDto.getDepositPercentage() != null ? requestDto.getDepositPercentage() : 20)
                .status(requestDto.getStatus() != null ? requestDto.getStatus() : "ACTIVE")
                .owner(owner)
                .images(new ArrayList<>())
                .build();

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