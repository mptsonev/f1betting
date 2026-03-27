package com.sporty.cache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

import com.sporty.client.F1EventsClient;
import com.sporty.model.Driver;
import com.sporty.model.Session;

@Component
public class F1EventsCacheInMemory implements F1EventsCache {
    private final F1EventsClient f1EventsClient;

    private volatile List<Session> sessions = List.of();
    private final ConcurrentMap<Integer, List<Driver>> driversBySessionKey = new ConcurrentHashMap<>();

    public F1EventsCacheInMemory(F1EventsClient f1EventsClient) {
        this.f1EventsClient = f1EventsClient;
    }

    @Override
    public void updateSessions(List<Session> sessions) {
        this.sessions = List.copyOf(sessions);
    }

    @Override
    public List<Session> getSessions() {
        return sessions;
    }

    @Override
    public List<Driver> getDriversForSession(int sessionKey) {
        return driversBySessionKey.computeIfAbsent(sessionKey,
                key -> List.copyOf(f1EventsClient.fetchDriversBySessionKey(key)));
    }
}
