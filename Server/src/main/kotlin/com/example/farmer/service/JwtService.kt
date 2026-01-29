package com.example.farmer.service

import com.example.farmer.entity.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtService {

    private val secretKey = Keys.hmacShaKeyFor("qwertyuiop1234567890123454321adsaf".toByteArray())
    private val expirationTime = 86400000*2 // 24 часа в миллисекундах


    //Генерация токена
    fun generateToken(user: User): String {
        val claims = mapOf("role" to user.role)

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(user.email)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expirationTime))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    //Извлечение email (subject) из токена
    fun extractEmail(token: String): String? {
        return extractAllClaims(token).subject
    }

    //Проверка токена принадлежит юзеру и не просрочен
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val email = extractEmail(token)
        return (email == userDetails.username && !isTokenExpired(token))
    }

    //Проверка на подлинность по времени
    private fun isTokenExpired(token: String): Boolean {
        return extractAllClaims(token).expiration.before(Date())
    }

    //Достаем все параметры из токена
    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }
}