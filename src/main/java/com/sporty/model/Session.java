package com.sporty.model;

public record Session(
        int sessionKey,
        String sessionName,
        String sessionType,
        String countryName,
        String countryCode,
        int countryKey,
        int circuitKey,
        String circuitShortName,
        String dateStart,
        String dateEnd,
        String gmtOffset,
        String location,
        int meetingKey,
        int year) {
}
