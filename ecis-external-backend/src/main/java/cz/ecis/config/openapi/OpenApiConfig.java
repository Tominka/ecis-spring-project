package cz.ecis.config.openapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("ECIS Extenal API")
                .description("External REST API for ECIS")
                .version("1.0.0")
            )
            .components(new Components()
                .addSecuritySchemes("apiKeyContext",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(In.HEADER)
                        .name("X-API-Key")
                )
            );
    }
}
