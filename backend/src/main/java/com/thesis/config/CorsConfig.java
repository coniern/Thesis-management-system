package com.thesis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许的源
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://localhost:5175");
        configuration.addAllowedOrigin("http://localhost:5180");
        // 允许的HTTP方法
        configuration.addAllowedMethod("*");
        // 允许的请求头
        configuration.addAllowedHeader("*");
        // 允许携带凭证
        configuration.setAllowCredentials(true);
        // 预检请求的有效期（秒）
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 应用到所有路径
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }
}
