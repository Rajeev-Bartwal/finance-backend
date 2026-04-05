package com.finance.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Checking Health of the App.")
public class HealthController {

    @GetMapping("/health")
    @Operation(
            summary = "Hit to check health of the app"
    )
    public String health() {
        return "OK!...Health is Good...";
    }

}
