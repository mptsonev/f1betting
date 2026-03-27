package com.sporty.cache;

import java.math.BigDecimal;

public interface UserBalanceCache {

    BigDecimal getBalance(int userId);

    void updatePosition(int userId, BigDecimal delta);
}
