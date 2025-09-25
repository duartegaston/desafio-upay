package com.example.api.controller;

import com.example.api.dto.SumRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calculator")
public class CalculatorController {

    @PostMapping("/sum")
    public ResponseEntity<Double> sum(@Valid @RequestBody SumRequest req) {
        Double res = req.getNumber1() + req.getNumber2();
        return ResponseEntity.ok(res);
    }
}
