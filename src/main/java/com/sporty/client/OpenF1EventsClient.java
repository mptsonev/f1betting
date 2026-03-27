package com.sporty.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.sporty.client.dto.OpenF1DriverResponse;
import com.sporty.client.dto.OpenF1SessionResponse;
import com.sporty.model.Driver;
import com.sporty.model.Session;

@Component
public class OpenF1EventsClient implements F1EventsClient {
    private final RestClient restClient;

    public OpenF1EventsClient(@Value("${openf1.api.base-url}") @NonNull String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public List<Session> fetchSessions() {
        OpenF1SessionResponse[] response = restClient.get()
                .uri("/v1/sessions")
                .retrieve()
                .body(OpenF1SessionResponse[].class);

        return response == null ? List.of() : Arrays.stream(response)
                .map(OpenF1SessionResponse::toDomain)
                .toList();
    }

    @Override
    public List<Driver> fetchDriversBySessionKey(int sessionKey) {
        OpenF1DriverResponse[] response = restClient.get()
                .uri("/v1/drivers?session_key={sessionKey}", sessionKey)
                .retrieve()
                .body(OpenF1DriverResponse[].class);

        return response == null ? List.of() : Arrays.stream(response)
                .map(OpenF1DriverResponse::toDomain)
                .toList();
    }
}
