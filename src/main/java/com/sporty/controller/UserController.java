package com.sporty.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sporty.model.Bet;
import com.sporty.service.BalanceService;
import com.sporty.service.BetService;

import lombok.AllArgsConstructor;


@RestController
@RequestMapping("api/v1/user/{userId}")
@AllArgsConstructor
public class UserController {

    private final BalanceService balanceService;
    private final BetService betService;

    @GetMapping("balance")
    public ResponseEntity<BigDecimal> getUserBalance(@PathVariable int userId) {
        BigDecimal balance = balanceService.getBalance(userId);
        if (balance == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(balance);
    }

    @GetMapping("bets")
    public ResponseEntity<List<Bet>> getUserBetsEntity(@PathVariable int userId) {
        List<Bet> bets = betService.getBetByUserId(userId);
        return ResponseEntity.ok(bets);
    }
    

}
