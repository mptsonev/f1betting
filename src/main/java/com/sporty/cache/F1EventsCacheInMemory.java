package com.sporty.cache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

import com.sporty.client.F1EventsClient;
import com.sporty.model.DriverMarketEntry;
import com.sporty.model.Session;

@Component
public class F1EventsCacheInMemory implements F1EventsCache {
    private final F1EventsClient f1EventsClient;

    private volatile List<Session> sessions = List.of();
    private final ConcurrentMap<Integer, List<DriverMarketEntry>> driverMarketBySessionKey = new ConcurrentHashMap<>();

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
    public List<DriverMarketEntry> getDriverMarketForSession(int sessionKey) {
        return driverMarketBySessionKey.computeIfAbsent(sessionKey, key ->
                f1EventsClient.fetchDriversBySessionKey(key).stream()
                        .map(driver -> new DriverMarketEntry(
                                driver.driverNumber(),
                                driver.fullName(),
                                ThreadLocalRandom.current().nextInt(2, 5)))
                        .toList());
    }
}
