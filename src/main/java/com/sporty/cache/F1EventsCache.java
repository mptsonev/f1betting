package com.sporty.cache;

import java.util.List;

import com.sporty.model.Driver;
import com.sporty.model.Session;

public interface F1EventsCache {

    void updateSessions(List<Session> sessions);

    List<Session> getSessions();

    List<Driver> getDriversForSession(int sessionKey);
}
