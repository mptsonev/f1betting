package com.sporty.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Bet(
        UUID id,
        int userId,
        int sessionKey,
        int driverId,
        BigDecimal amount,
        int odds,
        BetStatus status,
        Instant createdAt) {
}
