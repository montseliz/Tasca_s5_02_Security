package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Dice Game API", version = "1.0.0", description = "Documentation Dice Game RESTful API"), security = @SecurityRequirement(name = "bearerToken"))
@SecurityScheme(name = "bearerToken", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfiguration {

    public static final String[] PUBLIC_PATHS = {"/swagger-ui/index.html", "/swagger-ui.html","/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**", "/webjars/**", "/swagger-resources/**", "/api/auth/**"};

}
