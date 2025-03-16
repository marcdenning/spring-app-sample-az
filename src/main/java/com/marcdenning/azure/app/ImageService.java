package com.marcdenning.azure.app;

import org.springframework.core.io.Resource;

import java.io.IOException;

public interface ImageService {

    void uploadImage(String imageName, Resource imageData) throws IOException;

    Resource downloadImage(String imageName);
}
