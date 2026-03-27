package com.sporty.stub;

import java.util.List;

import com.sporty.client.F1EventsClient;
import com.sporty.model.Driver;
import com.sporty.model.Session;

public class StubF1EventsClient implements F1EventsClient {

    public static final Session BAHRAIN_RACE = new Session(
            9472, "Race", "Race", "Bahrain", "BHR", 1,
            1, "Sakhir", "2024-03-02T15:00:00", "2024-03-02T17:00:00",
            "+03:00", "Sakhir", 1, 2024);

    public static final Session AUSTRALIA_QUALIFYING = new Session(
            9480, "Qualifying", "Qualifying", "Australia", "AUS", 2,
            2, "Melbourne", "2024-03-23T16:00:00", "2024-03-23T17:00:00",
            "+11:00", "Melbourne", 2, 2024);

    public static final Driver VERSTAPPEN = new Driver(1, "Max", "Verstappen", "Max Verstappen", "VER", 9472);
    public static final Driver HAMILTON = new Driver(44, "Lewis", "Hamilton", "Lewis Hamilton", "HAM", 9472);

    @Override
    public List<Session> fetchSessions(String sessionType, String year, String countryName) {
        return List.of(BAHRAIN_RACE, AUSTRALIA_QUALIFYING);
    }

    @Override
    public List<Driver> fetchDriversBySessionKey(int sessionKey) {
        if (sessionKey == 9472) {
            return List.of(VERSTAPPEN, HAMILTON);
        }
        return List.of();
    }
}
