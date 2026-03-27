package com.sporty.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sporty.service.BalanceService;

import org.springframework.web.bind.annotation.PathVariable;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/v1/balance")
@AllArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("user/{userId}")
    public ResponseEntity<BigDecimal> getUserBalance(@PathVariable int userId) {
        BigDecimal balance = balanceService.getBalance(userId);
        if (balance == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(balance);
    }

}
