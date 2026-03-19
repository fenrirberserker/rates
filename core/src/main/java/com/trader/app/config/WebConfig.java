package com.trader.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Global CORS configuration for Spring WebFlux.
 *
 * Using WebFluxConfigurer instead of @CrossOrigin on controllers because
 * @CrossOrigin does not reliably resolve ${...} property placeholders in
 * the WebFlux annotation processing pipeline.
 */
@Configuration
public class WebConfig implements WebFluxConfigurer {

    @Value("${trading.api.allowed-origins}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*");
    }
}