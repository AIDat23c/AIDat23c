package com.example.aidat23c.service;

import com.example.aidat23c.dtos.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
public class BettingApiService {

    private static final Logger logger = LoggerFactory.getLogger(BettingApiService.class);

    @Value("${app.betting-api-url}")
    private String bettingApiUrl;

    @Value("${app.betting-api-key}")
    private String bettingApiKey;

    private final WebClient webClient;

    public BettingApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(bettingApiUrl).build();
    }

    public List<Event> fetchBettingData() {
        try {
            String jsonResponse = webClient.get()
                    .uri("/")
                    .header("Authorization", "Bearer " + bettingApiKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)  // Fetch as String to inspect raw JSON
                    .block();

            // Log the raw JSON response
            logger.debug("Raw JSON response from betting API:\n" + jsonResponse);

            // Deserialize JSON into Event array
            ObjectMapper mapper = new ObjectMapper();
            Event[] eventsArray = mapper.readValue(jsonResponse, Event[].class);
            List<Event> events = Arrays.asList(eventsArray);

            logger.debug("Deserialized events: " + events);
            return events;
        } catch (Exception e) {
            logger.error("Failed to fetch betting data", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch betting data", e);
        }
    }
}
