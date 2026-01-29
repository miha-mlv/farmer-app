package com.example.farmer.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(private val mailSender: JavaMailSender) {

    fun sendVerificationCode(to: String, code: String) {
        val message = SimpleMailMessage()
        message.setTo(to)
        message.subject = "Код подтверждения регистрации"
        message.text = "Ваш код для входа: $code. Никому не сообщайте его."
        message.from = "malorodov7@gmail.com"

        mailSender.send(message)
    }
}