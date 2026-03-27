package com.sporty.model;

public record Driver(
        int driverNumber,
        String firstName,
        String lastName,
        String fullName,
        String nameAcronym,
        int sessionKey) {
}
