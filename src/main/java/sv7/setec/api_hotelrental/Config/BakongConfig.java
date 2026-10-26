package sv7.setec.api_hotelrental.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bakong")
@Data
public class BakongConfig {
    private String bakongId = "chey_kimrith@bkrt";
    private String merchantName = "Ecommerce";
    private String merchantCity = "Phnom Penh";
    private String acquiringId = "chey_kimrith@bkrt";
    private String apiBaseUrl = "https://api-bakong.nbc.gov.kh";
    private String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjp7ImlkIjoiZDEyOTZhYzQ2MmFiNGY0OCJ9LCJpYXQiOjE3ODcwNzEyNTcsImV4cCI6MTc5NDg0NzI1N30.qOuwDx-uMqMfcMnyjfOzrx_je8G33ryBP9TUycG4nJU";
}