package com.sporty.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sporty.cache.F1EventsCache;
import com.sporty.dto.F1EventResponseDTO;
import com.sporty.model.DriverMarketEntry;
import com.sporty.model.Session;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EventsService {
    private final F1EventsCache f1EventsCache;

    private static final Set<String> VALID_SESSION_TYPES = Set.of(
            "practice", "qualifying", "race", "sprint", "sprint qualifying", "sprint shootout");

    public List<F1EventResponseDTO> getFilteredEvents(String sessionType, String year, String country) {
        this.validateFilters(sessionType, year, country);

        return f1EventsCache.getSessions().stream()
                .filter(s -> sessionType == null || s.sessionType().equalsIgnoreCase(sessionType))
                .filter(s -> year == null || String.valueOf(s.year()).equals(year))
                .filter(s -> country == null || s.countryName().equalsIgnoreCase(country))
                .map(this::toEventResponseDTO)
                .toList();
    }

    public List<DriverMarketEntry> getDriverMarket(int sessionKey) {
        return f1EventsCache.getDriverMarketForSession(sessionKey);
    }

    private void validateFilters(String sessionType, String year, String country) {
        if (sessionType != null && !VALID_SESSION_TYPES.contains(sessionType.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Invalid sessionType: '%s'. Valid values are: %s".formatted(sessionType, VALID_SESSION_TYPES));
        }
        if (year != null && !year.matches("\\d{4}")) {
            throw new IllegalArgumentException("Invalid year: '%s'. Must be a 4-digit year (e.g. 2024)".formatted(year));
        }
        if (country != null && country.isBlank()) {
            throw new IllegalArgumentException("Country filter cannot be blank");
        }
    }

    private F1EventResponseDTO toEventResponseDTO(Session session) {
        List<DriverMarketEntry> driverMarket = f1EventsCache.getDriverMarketForSession(session.sessionKey());

        return new F1EventResponseDTO(
                session.sessionKey(),
                session.sessionName(),
                session.sessionType(),
                session.countryName(),
                session.circuitShortName(),
                session.dateStart(),
                session.dateEnd(),
                session.year(),
                driverMarket);
    }
}
