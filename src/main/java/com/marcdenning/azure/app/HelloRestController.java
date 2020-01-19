package com.marcdenning.azure.app;

import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Log
@RestController
@RequestMapping(path = "/api/hello")
public class HelloRestController {

    @GetMapping
    public ResponseEntity<Map<String, String>> hello() {
        final Map<String, String> response = new HashMap<>();

        response.put("hello", "world");
        log.info("Hello world");
        return ResponseEntity.ok(response);
    }
}
