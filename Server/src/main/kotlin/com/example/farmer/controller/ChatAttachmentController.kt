package com.example.farmer.controller

import com.google.rpc.context.AttributeContext
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths

@RestController
@RequestMapping("/api/chat/attachments")
class ChatAttachmentController {

    // Путь, куда будут сохраняться фото (создай эту папку в корне проекта)
    private val uploadDir = "uploads/chat/"

    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, String>> {
        if (file.isEmpty) return ResponseEntity.badRequest().build()

        try {
            val fileName = "${System.currentTimeMillis()}_${file.originalFilename}"
            val path = Paths.get(uploadDir + fileName)

            // Создаем директории, если их нет
            Files.createDirectories(path.parent)
            Files.write(path, file.bytes)

            // Возвращаем URL, по которому Android сможет скачать это фото
            val fileUrl = "/api/chat/attachments/download/$fileName"
            return ResponseEntity.ok(mapOf("url" to fileUrl))
        } catch (e: Exception) {
            return ResponseEntity.internalServerError().build()
        }
    }

    // Метод для того, чтобы Android мог отобразить фото по ссылке
    @GetMapping("/download/{fileName}")
    fun getFile(@PathVariable fileName: String): ResponseEntity<Resource> {
        val path = Paths.get(uploadDir + fileName)
        val resource = UrlResource(path.toUri())
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(resource)
    }
}