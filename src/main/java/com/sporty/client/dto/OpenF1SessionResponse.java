package com.sporty.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sporty.model.Session;

public record OpenF1SessionResponse(
        @JsonProperty("session_key") int sessionKey,
        @JsonProperty("session_name") String sessionName,
        @JsonProperty("session_type") String sessionType,
        @JsonProperty("country_name") String countryName,
        @JsonProperty("country_code") String countryCode,
        @JsonProperty("country_key") int countryKey,
        @JsonProperty("circuit_key") int circuitKey,
        @JsonProperty("circuit_short_name") String circuitShortName,
        @JsonProperty("date_start") String dateStart,
        @JsonProperty("date_end") String dateEnd,
        @JsonProperty("gmt_offset") String gmtOffset,
        @JsonProperty("location") String location,
        @JsonProperty("meeting_key") int meetingKey,
        @JsonProperty("year") int year) {

    public Session toDomain() {
        return new Session(sessionKey, sessionName, sessionType, countryName, countryCode,
                countryKey, circuitKey, circuitShortName, dateStart, dateEnd, gmtOffset,
                location, meetingKey, year);
    }
}
