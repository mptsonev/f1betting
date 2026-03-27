package com.sporty.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record F1BetRequestDTO(
        @NotNull Integer userId,
        @NotNull Integer sessionKey,
        @NotNull Integer driverId,
        @NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal amount) {
}
