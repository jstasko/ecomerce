package sk.stasko.ecomerce.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class ImageUploadService {

    public String uploadImage(String pathToSaveImage, MultipartFile image) throws IOException, IllegalArgumentException {
        // Get the original file name
        String originalFileName = image.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("Invalid image file. Original filename is missing.");
        }

        // Generate a unique file name
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));

        // Construct the full file path
        String filePath = pathToSaveImage + File.separator + fileName;

        // Ensure the directory exists and is writable
        ensureWritableDirectory(Paths.get(pathToSaveImage));

        // Upload the file to the server
        Files.copy(image.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    private void ensureWritableDirectory(Path dirPath) throws IOException {
        if (!Files.exists(dirPath)) {
            log.info("Directory does not exist. Creating: {}", dirPath);
            try {
                Files.createDirectories(dirPath);
                log.info("Directory created successfully: {}", dirPath);
            } catch (IOException e) {
                throw new IOException("Failed to create directory: " + e.getMessage(), e);
            }
        } else if (!Files.isWritable(dirPath)) {
            throw new IOException("Directory exists but is not writable: " + dirPath);
        }
    }
}
