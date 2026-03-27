package com.sporty.cache;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

@Service
public class UserBalanceCacheInMemory implements UserBalanceCache {

    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("100");
    private final ConcurrentMap<Integer, BigDecimal> balances = new ConcurrentHashMap<>();

    public UserBalanceCacheInMemory() {
        IntStream.rangeClosed(1, 100)
                .forEach(id -> balances.put(id, INITIAL_BALANCE));
    }

    @Override
    public BigDecimal getBalance(int userId) {
        return balances.get(userId);
    }

    @Override
    public void updatePosition(int userId, BigDecimal delta) {
        balances.compute(userId, (key, current) -> {
            if (current == null) {
                throw new IllegalArgumentException("User not found: " + userId);
            }
            BigDecimal newBalance = current.add(delta);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Insufficient balance for user: " + userId);
            }
            return newBalance;
        });
    }
}
