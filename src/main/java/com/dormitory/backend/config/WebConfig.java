package com.dormitory.backend.config;


import com.dormitory.backend.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public LoginInterceptor getLoginIntercepter() {
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(getLoginIntercepter()).addPathPatterns("/api/*").excludePathPatterns("/api/login","/api/register","/swagger-ui.html", "/v2/api-docs", "/swagger-resources/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String allowedIp = "http://8.138.84.46";
        registry.addMapping("/**")
                .allowedOrigins(allowedIp)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                .maxAge(1800)
                .allowedHeaders("*");
    }

}
