package com.sporty.dto;

import jakarta.validation.constraints.NotNull;

public record F1SimulateEventRequestDTO(
        @NotNull Integer sessionId,
        @NotNull Integer winnerDriverId) {
}
