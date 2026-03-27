package com.sporty;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.service.BalanceService;
import com.sporty.stub.StubF1EventsClientConfig;

@SpringBootTest(properties = "f1.cache.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(StubF1EventsClientConfig.class)
class BettingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BalanceService balanceService;

    @Test
    void fullBettingFlow_placeBetSimulateAndCheckBalance() throws Exception {
        int userId = 1;

        // 1. List events to populate driver market cache
        mockMvc.perform(get("/api/v1/f1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sessionKey").value(9472))
                .andExpect(jsonPath("$[0].driverMarket").isNotEmpty())
                .andExpect(jsonPath("$[1].sessionKey").value(9480));

        // 2. Place a bet on Verstappen (driverId=1) in Bahrain Race (sessionKey=9472)
        MvcResult betResult = mockMvc.perform(post("/api/v1/f1/bet")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {"userId": 1, "sessionKey": 9472, "driverId": 1, "amount": 100.00}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.sessionKey").value(9472))
                .andExpect(jsonPath("$.driverId").value(1))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.odds").isNumber())
                .andReturn();

        JsonNode betResponse = objectMapper.readTree(betResult.getResponse().getContentAsString());
        int odds = betResponse.get("odds").asInt();

        // 3. Verify balance decreased by bet amount (1000 - 100 = 900)
        assertThat(balanceService.getBalance(userId)).isEqualByComparingTo(new BigDecimal("900"));

        // 4. Simulate event — Verstappen wins
        mockMvc.perform(post("/api/v1/f1/simulate")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {"sessionId": 9472, "winnerDriverId": 1}
                                """))
                .andExpect(status().isOk());

        // 5. Verify balance: 900 + (100 * odds) = 900 + payout
        BigDecimal expectedBalance = new BigDecimal("900").add(new BigDecimal("100").multiply(BigDecimal.valueOf(odds)));
        assertThat(balanceService.getBalance(userId)).isEqualByComparingTo(expectedBalance);
    }

    @Test
    void placeBet_insufficientBalance_returns400() throws Exception {
        // Populate cache first
        mockMvc.perform(get("/api/v1/f1/events")).andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/f1/bet")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {"userId": 2, "sessionKey": 9472, "driverId": 1, "amount": 9999.00}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Insufficient balance for user: 2"));
    }

    @Test
    void placeBet_invalidDriver_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/f1/events")).andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/f1/bet")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {"userId": 3, "sessionKey": 9472, "driverId": 999, "amount": 50.00}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Driver 999 not found in session 9472"));
    }

    @Test
    void placeBet_invalidUser_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/f1/bet")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {"userId": 999, "sessionKey": 9472, "driverId": 1, "amount": 50.00}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User not found: 999"));
    }

    @Test
    void listEvents_filterByCountry() throws Exception {
        mockMvc.perform(get("/api/v1/f1/events").param("countryName", "Bahrain"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].countryName").value("Bahrain"));
    }

    @Test
    void listEvents_filterBySessionType() throws Exception {
        mockMvc.perform(get("/api/v1/f1/events").param("sessionType", "Qualifying"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].sessionType").value("Qualifying"));
    }

    @Test
    void listEvents_invalidSessionType_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/f1/events").param("sessionType", "invalid"))
                .andExpect(status().isBadRequest());
    }
}
