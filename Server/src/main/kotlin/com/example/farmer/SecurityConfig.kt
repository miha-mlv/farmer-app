package com.example.farmer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Paths

@Configuration
class SecurityConfig {
    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // Обязательно для Postman/Android
            .authorizeHttpRequests { auth ->
                // Разрешаем всем доступ к регистрации и верификации
                auth.requestMatchers("/api/**").permitAll()
                auth.requestMatchers("/images/**").permitAll()
                // Все остальные запросы (товары, заказы) требуют токен
                auth.anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

        return http.build()
    }
}


@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // Получаем абсолютный путь к вашей папке uploads
        val uploadPath = Paths.get("uploads/products").toAbsolutePath().toString()

        registry.addResourceHandler("/images/**")
            .addResourceLocations("file:$uploadPath/")
    }
}