package sv7.setec.api_hotelrental.Feature.user;

import sv7.setec.api_hotelrental.Feature.user.Dtos.AuthResponseDto;
import sv7.setec.api_hotelrental.Feature.user.Dtos.UserLoginDto;
import sv7.setec.api_hotelrental.Feature.user.Dtos.UserRegisterDto;
import sv7.setec.api_hotelrental.Feature.user.Dtos.UserResponseDto;

import java.util.List;

public interface UserService {
    AuthResponseDto register(UserRegisterDto registerDto);
    AuthResponseDto login(UserLoginDto loginDto);
    UserResponseDto getUserById(Long id);
    List<UserResponseDto> getAllUsers();
}