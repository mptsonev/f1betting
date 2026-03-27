package com.sporty.client.ratelimit;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

@Component
public class OpenF1RateLimiter {

    private final Semaphore perSecondLimiter;
    private final Semaphore perMinuteLimiter;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "openf1-rate-limiter");
        t.setDaemon(true);
        return t;
    });

    public OpenF1RateLimiter(OpenF1RateLimitConfig config) {
        this.perSecondLimiter = new Semaphore(config.maxRequestsPerSecond());
        this.perMinuteLimiter = new Semaphore(config.maxRequestsPerMinute());
    }

    public <T> T execute(Supplier<T> request) throws InterruptedException {
        perSecondLimiter.acquire();
        perMinuteLimiter.acquire();
        try {
            return request.get();
        } finally {
            scheduler.schedule(() -> perSecondLimiter.release(), 1, TimeUnit.SECONDS);
            scheduler.schedule(() -> perMinuteLimiter.release(), 1, TimeUnit.MINUTES);
        }
    }
}
