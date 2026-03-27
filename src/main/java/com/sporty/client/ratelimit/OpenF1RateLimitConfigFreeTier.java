package com.sporty.client.ratelimit;

import org.springframework.stereotype.Component;

@Component
public class OpenF1RateLimitConfigFreeTier implements OpenF1RateLimitConfig {

    @Override
    public Integer maxRequestsPerSecond() {
        return 3;
    }

    @Override
    public Integer maxRequestsPerMinute() {
        return 30;
    }
}
