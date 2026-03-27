package com.sporty.stub;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.sporty.client.F1EventsClient;

@TestConfiguration
public class StubF1EventsClientConfig {

    @Bean
    @Primary
    public F1EventsClient f1EventsClient() {
        return new StubF1EventsClient();
    }
}
