package com.marcdenning.azure.app;

import jakarta.websocket.server.PathParam;
import lombok.extern.java.Log;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.logging.Level;

@Log
@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/{imageName:.+}")
    public ResponseEntity uploadImage(@RequestBody Resource imageResource, @PathVariable("imageName") final String imageName) {
        try {
            imageService.uploadImage(imageName, imageResource);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error uploading file: " + e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{imageName:.+}")
    public ResponseEntity<Resource> downloadImage(@PathVariable("imageName") final String imageName) {
        final Resource image = imageService.downloadImage(imageName);

        if (!image.exists()) {
            log.log(Level.WARNING, "Image not found: " + imageName);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.OK)
            .header("Content-Type", MediaType.IMAGE_JPEG_VALUE)
            .body(image);
    }
}
