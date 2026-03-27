package com.sporty.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record F1BetResponseDTO(
        UUID betId,
        int userId,
        int sessionKey,
        int driverId,
        BigDecimal amount,
        int odds,
        String status) {
}
