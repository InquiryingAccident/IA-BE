package com.suhkang.inquiryingaccident.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "📚 PLANE ACCIDENT FINDER 📚",
        description = """
            ### 🌐 PLANE ACCIDENT FINDER 🌐 : http:suh-project.synology.me:8082
            """,
        version = "1.0v"
    ),
    servers = {
        @Server(url = "http:suh-project.synology.me:8082", description = "메인 서버"),
        @Server(url = "http://localhost:8080", description = "로컬 서버")
    }
)
//@Profile("dev")
@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    // cookie 세션 인증 스키마 문서 선언
    SecurityScheme cookieAuthScheme = new SecurityScheme()
        .type(SecurityScheme.Type.APIKEY)
        .in(SecurityScheme.In.COOKIE)
        .name("JSESSIONID"); // 세션 쿠키 이름

    SecurityRequirement securityRequirement
        = new SecurityRequirement().addList("sessionCookie");

    return new OpenAPI()
        .components(new Components()
            .addSecuritySchemes("sessionCookie", cookieAuthScheme)
        )
        .addSecurityItem(securityRequirement)
        .servers(List.of(
                new io.swagger.v3.oas.models.servers.Server()
                    .url("http://localhost:8090")
                    .description("로컬 서버"),
                new io.swagger.v3.oas.models.servers.Server()
                    .url("http:suh-project.synology.me:8082")
                    .description("메인 서버")
            )
        );
  }
}
