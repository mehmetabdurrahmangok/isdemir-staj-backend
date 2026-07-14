package com.isdemirstaj.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Bu sınıfın Spring Boot için bir konfigürasyon sınıfı olduğunu belirtir
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Yazdığımız JwtInterceptor güvenlik duvarını tüm /api/ ile başlayan adreslere uyguluyoruz
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**");
    }
}