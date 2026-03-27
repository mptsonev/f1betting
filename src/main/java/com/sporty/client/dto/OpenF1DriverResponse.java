package com.sporty.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sporty.model.Driver;

public record OpenF1DriverResponse(
        @JsonProperty("driver_number") int driverNumber,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("name_acronym") String nameAcronym,
        @JsonProperty("session_key") int sessionKey) {

    public Driver toDomain() {
        return new Driver(driverNumber, firstName, lastName, fullName, nameAcronym, sessionKey);
    }
}
