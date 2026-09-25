package com.vinayuttekar.ecommerce.service.impl;

import com.vinayuttekar.ecommerce.exception.IllegalFileException;
import com.vinayuttekar.ecommerce.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalFileException("Image file is required");
        if (file.getSize() > MAX_FILE_SIZE) throw new IllegalFileException("Image must not exceed 5 MB");
        if (!ALLOWED_TYPES.contains(file.getContentType())) throw new IllegalFileException("Only JPG, PNG and WEBP images are allowed");

        String original = file.getOriginalFilename();
        String extension = original == null ? "" : original.substring(original.lastIndexOf('.')).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension)) throw new IllegalFileException("Invalid image extension");

        Path folder = Paths.get(path).toAbsolutePath().normalize();
        Files.createDirectories(folder);
        String fileName = UUID.randomUUID() + extension;
        Path target = folder.resolve(fileName).normalize();
        if (!target.getParent().equals(folder)) throw new IllegalFileException("Invalid file path");
        Files.copy(file.getInputStream(), target);
        return fileName;
    }

    @Override
    public void deleteImage(String path, String fileName) throws IOException {
        if (fileName == null || fileName.isBlank()) {
            return;
        }

        Path folder =
                Paths.get(path)
                        .toAbsolutePath()
                        .normalize();

        Path target =
                folder.resolve(fileName)
                        .normalize();

        if (!target.getParent().equals(folder)) {
            throw new IllegalFileException("Invalid file path");
        }

        Files.deleteIfExists(target);
    }
}
