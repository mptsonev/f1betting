package com.sporty.cron;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sporty.cache.F1EventsCache;
import com.sporty.client.F1EventsClient;
import com.sporty.model.Session;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Component
@ConditionalOnProperty(name = "f1.cache.enabled", havingValue = "true", matchIfMissing = true)
@AllArgsConstructor
public class F1EventsUpdateJob {
    private static final Logger log = LoggerFactory.getLogger(F1EventsUpdateJob.class);

    private final F1EventsClient f1EventsClient;
    private final F1EventsCache f1EventsCache;

    @PostConstruct
    public void init() {
        refreshCache();
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void refreshCache() {
        log.info("Refreshing F1 events cache...");
        List<Session> sessions = f1EventsClient.fetchSessions();
        f1EventsCache.updateSessions(sessions);
        log.info("Cache refreshed with {} sessions. Drivers will be loaded on demand.", sessions.size());
    }
}
