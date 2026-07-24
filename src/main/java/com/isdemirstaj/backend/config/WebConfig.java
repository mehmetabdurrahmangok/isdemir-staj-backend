package com.isdemirstaj.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. Tüm sistemi (/api/**) JWT ile koru
        // 2. Sadece yapay zeka servislerini (/api/ai/**) güvenlik duvarından muaf tut
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**");
              //  .excludePathPatterns("/api/ai/**");
    }
}