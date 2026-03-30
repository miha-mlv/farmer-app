package com.example.farmer.repository

import com.example.farmer.entity.User
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {

    fun findByEmail(email: String): Optional<User>
    fun existsByEmail(email: String): Boolean

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isEnabled = :isEnabled WHERE u.email = :email")
    fun updateIsEnabled(email: String, isEnabled: Boolean)

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.fcmToken = :fcmToken WHERE u.email = :email")
    fun updateFcmToken(fcmToken: String, email: String)


}