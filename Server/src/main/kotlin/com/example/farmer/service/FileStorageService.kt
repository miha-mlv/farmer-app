package com.example.farmer.service

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID

@Service
class FileStorageService {
    private val uploadDir = "uploads/products"

    fun saveFile(file: MultipartFile): String {
        val directory = File(uploadDir)
        if(!directory.exists()) directory.mkdirs()
        val fileName = "${UUID.randomUUID()}_${file.originalFilename}"
        val path = Paths.get(uploadDir).resolve(fileName)
        Files.createDirectories(path.parent)
        Files.copy(file.inputStream, path, StandardCopyOption.REPLACE_EXISTING)
        return fileName
    }
}