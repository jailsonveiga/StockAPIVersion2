package com.jay.stockapiv2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @Autowired
    Environment env;


    @GetMapping("/")
    public ResponseEntity<?> rootRoute() {
        return ResponseEntity.ok("Root Route");
    }
}
