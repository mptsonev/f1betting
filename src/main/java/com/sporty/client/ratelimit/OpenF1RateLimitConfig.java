package com.sporty.client.ratelimit;

public interface OpenF1RateLimitConfig {

    Integer maxRequestsPerSecond();

    Integer maxRequestsPerMinute();

}
