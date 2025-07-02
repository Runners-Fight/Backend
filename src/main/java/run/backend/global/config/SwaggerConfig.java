package run.backend.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Runners-Fight API",
                description = "Runners-Fight 서비스의 API 명세서입니다.",
                version = "v1"
        )
)
public class SwaggerConfig {

    @Value("${swagger.server.url.prod}")
    private String prodUrl;

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("jwt")
        );

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components)
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("local server"),
                        new Server().url(prodUrl).description("production server")
                ));
    }
}
