package com.sporty.dto;

import java.util.List;

import com.sporty.model.DriverMarketEntry;

public record F1EventResponseDTO(
        int sessionKey,
        String sessionName,
        String sessionType,
        String countryName,
        String circuitShortName,
        String dateStart,
        String dateEnd,
        int year,
        List<DriverMarketEntry> driverMarket) {
}
