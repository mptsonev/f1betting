package com.sporty.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.sporty.client.dto.OpenF1DriverResponse;
import com.sporty.client.dto.OpenF1SessionResponse;
import com.sporty.client.ratelimit.OpenF1RateLimiter;
import com.sporty.model.Driver;
import com.sporty.model.Session;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OpenF1EventsClient implements F1EventsClient {

        private final RestClient restClient;
        private final OpenF1RateLimiter rateLimiter;

        public OpenF1EventsClient(@Value("${openf1.api.base-url}") @NonNull String baseUrl,
                        OpenF1RateLimiter rateLimiter) {
                this.restClient = RestClient.builder()
                                .baseUrl(baseUrl)
                                .build();
                this.rateLimiter = rateLimiter;
        }

        @Override
        public List<Session> fetchSessions(String sessionType, String year, String countryName) {
                OpenF1SessionResponse[] response = executeRateLimited(() -> restClient.get()
                                .uri(uriBuilder -> {
                                        uriBuilder.path("/v1/sessions");
                                        if (sessionType != null) uriBuilder.queryParam("session_type", sessionType);
                                        if (year != null) uriBuilder.queryParam("year", year);
                                        if (countryName != null) uriBuilder.queryParam("country_name", countryName);
                                        return uriBuilder.build();
                                })
                                .retrieve()
                                .body(OpenF1SessionResponse[].class));

                return response == null ? List.of()
                                : Arrays.stream(response)
                                                .map(OpenF1SessionResponse::toDomain)
                                                .toList();
        }

        @Override
        public List<Driver> fetchDriversBySessionKey(int sessionKey) {
                log.info("Fetching drivers for session: {}", sessionKey);
                OpenF1DriverResponse[] response = executeRateLimited(() -> restClient.get()
                                .uri("/v1/drivers?session_key={sessionKey}", sessionKey)
                                .retrieve()
                                .body(OpenF1DriverResponse[].class));

                return response == null ? List.of()
                                : Arrays.stream(response)
                                                .map(OpenF1DriverResponse::toDomain)
                                                .toList();
        }

        private <T> T executeRateLimited(java.util.function.Supplier<T> request) {
                try {
                        return rateLimiter.execute(request);
                } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Rate-limited request interrupted", e);
                } catch (HttpClientErrorException ex) {
                        log.error("HTTP error during API call: {} - {}", ex.getStatusCode(),
                                        ex.getResponseBodyAsString());
                        return null;
                } catch (RestClientException ex) {
                        log.error("Error during API call: {}", ex.getMessage(), ex);
                        return null;
                }
        }
}
