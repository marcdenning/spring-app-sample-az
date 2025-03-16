package com.marcdenning.azure.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class AzureBlobImageService implements ImageService {

    private final ResourceLoader resourceLoader;

    private final String imageContainerName;

    @Autowired
    public AzureBlobImageService(ResourceLoader resourceLoader, @Value("${marcdenning.images.container-name}") String imageContainerName) {
        this.resourceLoader = resourceLoader;
        this.imageContainerName = imageContainerName;
    }

    @Override
    public void uploadImage(String imageName, Resource imageResource) throws IOException {
        try (final OutputStream outputStream = ((WritableResource) resourceLoader.getResource(getResourceUri(imageName))).getOutputStream();
             final InputStream inputStream = imageResource.getInputStream()) {
            inputStream.transferTo(outputStream);
        }
    }

    @Override
    public Resource downloadImage(String imageName) {
        return resourceLoader.getResource(getResourceUri(imageName));
    }

    private String getResourceUri(String imageName) {
        return "azure-blob://" + imageContainerName + "/" + imageName;
    }
}
