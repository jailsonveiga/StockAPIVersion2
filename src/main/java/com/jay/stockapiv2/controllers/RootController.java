package com.jay.stockapiv2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<?> rootRoute() {
        return ResponseEntity.ok("Root Route");
    }
}
