package com.sporty.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sporty.dto.F1BetRequestDTO;
import com.sporty.dto.F1EventResponseDTO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class F1Service {
    private final EventsService eventsService;
    private final BetService betService;
    private final BalanceService balanceService;

    public List<F1EventResponseDTO> getF1Events(String sessionType, String year, String country) {
        return eventsService.getFilteredEvents(sessionType, year, country);
    }

    public void placeBet(F1BetRequestDTO betRequest) {
    }

    public void simulateEventOutcome(String eventId) {
    }
}
