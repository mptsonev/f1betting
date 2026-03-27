package com.sporty.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sporty.dto.F1BetRequestDTO;
import com.sporty.dto.F1BetResponseDTO;
import com.sporty.dto.F1EventResponseDTO;
import com.sporty.dto.F1SimulateEventRequestDTO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class F1Service {
    private final EventsService eventsService;
    private final BetService betService;

    public List<F1EventResponseDTO> getF1Events(String sessionType, String year, String country) {
        return eventsService.getFilteredEvents(sessionType, year, country);
    }

    public F1BetResponseDTO placeBet(F1BetRequestDTO betRequest) {
        return betService.placeBet(betRequest);
    }

    public void simulateEventOutcome(F1SimulateEventRequestDTO settlementEvent) {
    }
}
