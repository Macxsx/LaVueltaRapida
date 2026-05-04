package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                        "http://localhost:4200",
                        "http://127.0.0.1:4200",
                        "http://localhost:5000",
                        "http://127.0.0.1:5000",
                        "https://hg6k75cd-4200.use2.devtunnels.ms",
                        "https://*.replit.dev",
                        "https://*.repl.co",
                        "https://*.devtunnels.ms"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH",
                                "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(true);
    }
}
