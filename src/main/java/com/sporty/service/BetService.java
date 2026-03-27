package com.sporty.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sporty.cache.BetCache;
import com.sporty.dto.F1BetRequestDTO;
import com.sporty.dto.F1BetResponseDTO;
import com.sporty.dto.F1SimulateEventRequestDTO;
import com.sporty.model.Bet;
import com.sporty.model.DriverMarketEntry;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BetService {

    private final BetCache betCache;
    private final BalanceService balanceService;
    private final EventsService eventsService;

    public F1BetResponseDTO placeBet(F1BetRequestDTO request) {
        BigDecimal balance = balanceService.getBalance(request.userId());
        if (balance.compareTo(request.amount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance for user: " + request.userId());
        }

        DriverMarketEntry marketEntry = eventsService.getDriverMarket(request.sessionKey()).stream()
                .filter(e -> e.driverId() == request.driverId())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Driver %d not found in session %d".formatted(request.driverId(), request.sessionKey())));

        Bet bet = new Bet(
                UUID.randomUUID(),
                request.userId(),
                request.sessionKey(),
                request.driverId(),
                request.amount(),
                marketEntry.odds(),
                Instant.now());

        balanceService.updatePosition(request.userId(), request.amount().negate());
        betCache.save(bet);

        return new F1BetResponseDTO(
                bet.id(),
                bet.userId(),
                bet.sessionKey(),
                bet.driverId(),
                bet.amount(),
                bet.odds());
    }

    public void settleBet(F1SimulateEventRequestDTO settlementEvent) {
        // get all bets for the session
        var bets = betCache.getBetsBySessionKey(settlementEvent.sessionId());
        for (var bet : bets) {
            if (bet.driverId() == settlementEvent.winnerDriverId()) {
                var payout = bet.amount().multiply(BigDecimal.valueOf(bet.odds()));
                balanceService.updatePosition(bet.userId(), payout);
            }
        }
        betCache.clearBetsBySessionKey(settlementEvent.sessionId());
    }
}
