package com.sporty.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sporty.dto.F1BetRequestDTO;
import com.sporty.dto.F1EventResponseDTO;
import com.sporty.dto.F1SimulateEventRequestDTO;
import com.sporty.service.F1Service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/v1/f1")
@AllArgsConstructor
public class F1Controller {

    private final F1Service f1Service;

    @GetMapping("events")
    public ResponseEntity<List<F1EventResponseDTO>> listF1Events(@RequestParam(required = false) String sessionType,
            @RequestParam(required = false) String year, @RequestParam(required = false) String country) {
        return ResponseEntity.ok(f1Service.getF1Events(sessionType, year, country));
    }

    @PostMapping("bet")
    public ResponseEntity<Void> placeBet(@RequestBody @Valid F1BetRequestDTO betRequest) {
        return null;
    }

    @PostMapping("simulate")
    public ResponseEntity<Void> simulateEventOutcome(@RequestBody @Valid F1SimulateEventRequestDTO simulateRequest) {
        return null;
    }

}
