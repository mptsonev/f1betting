package com.sporty.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.sporty.cache.UserBalanceCache;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BalanceService {

    private final UserBalanceCache userBalanceCache;

    public BigDecimal getBalance(int userId) {
        BigDecimal balance = userBalanceCache.getBalance(userId);
        if (balance == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return balance;
    }

    public void updatePosition(int userId, BigDecimal delta) {
        userBalanceCache.updatePosition(userId, delta);
    }
}
