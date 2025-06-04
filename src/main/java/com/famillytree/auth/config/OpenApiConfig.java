package com.famillytree.auth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Family Tree API")
                        .description("""
                            API pour la gestion de l'arbre généalogique familial.
                            
                            Cette API permet de :
                            - Gérer l'authentification des utilisateurs
                            - Gérer les tokens JWT
                            - Rafraîchir les tokens
                            - Vérifier la validité des tokens
                            
                            Pour utiliser l'API :
                            1. Créez un compte via /api/auth/register
                            2. Connectez-vous via /api/auth/login
                            3. Utilisez le token reçu pour les requêtes authentifiées
                            4. Rafraîchissez le token via /api/auth/refresh quand nécessaire
                            """)
                        .version("1.0")
                        .contact(new Contact()
                                .name("Family Tree Team")
                                .email("contact@familytree.com")
                                .url("https://familytree.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                    new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Serveur de développement")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .description("""
                    Pour utiliser l'authentification :
                    1. Obtenez un token via /api/auth/login
                    2. Ajoutez le token dans l'en-tête Authorization
                    3. Format : 'Bearer <votre_token>'
                    """);
    }
} 