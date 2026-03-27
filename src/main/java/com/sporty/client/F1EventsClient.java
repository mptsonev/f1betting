package com.sporty.client;

import java.util.List;

import com.sporty.model.Driver;
import com.sporty.model.Session;

public interface F1EventsClient {

    List<Session> fetchSessions(String sessionType, String year, String countryName);

    List<Driver> fetchDriversBySessionKey(int sessionKey);
}
