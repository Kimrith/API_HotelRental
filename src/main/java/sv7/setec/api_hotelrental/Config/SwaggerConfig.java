package Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI hotelRentalOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Hotel Rental Documentation")
                        .description("API documentation for the Hotel Rental application backend.")
                        .version("1.0.0"));
    }
}