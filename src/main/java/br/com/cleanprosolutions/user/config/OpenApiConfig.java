package br.com.cleanprosolutions.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger UI configuration for the user-service.
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Provides the OpenAPI metadata bean.
     *
     * @return configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI userServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clean Pro Solutions — User Service API")
                        .description("User profile management and geospatial proximity search service.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Clean Pro Solutions Team")
                                .url("https://cleanprosolutions.com.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
