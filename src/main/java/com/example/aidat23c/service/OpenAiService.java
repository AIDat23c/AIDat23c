package com.example.aidat23c.service;

import com.example.aidat23c.dtos.ChatCompletionRequest;
import com.example.aidat23c.dtos.ChatCompletionResponse;
import com.example.aidat23c.dtos.MyResponse;
import com.example.aidat23c.dtos.Event;
import com.example.aidat23c.dtos.Bookmaker;
import com.example.aidat23c.dtos.Market;
import com.example.aidat23c.dtos.Outcome;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Service
public class OpenAiService {

    public static final Logger logger = LoggerFactory.getLogger(OpenAiService.class);

    @Value("${app.api-key}")
    private String API_KEY;

    @Value("${app.url}")
    public String URL;

    @Value("${app.model}")
    public String MODEL;

    @Value("${app.temperature}")
    public double TEMPERATURE;

    @Value("${app.max_tokens}")
    public int MAX_TOKENS;

    @Value("${app.frequency_penalty}")
    public double FREQUENCY_PENALTY;

    @Value("${app.presence_penalty}")
    public double PRESENCE_PENALTY;

    @Value("${app.top_p}")
    public double TOP_P;

    @Value("${app.betting-api-url}")
    private String bettingApiUrl;

    @Value("${app.betting-api-key}")
    private String bettingApiKey;

    private WebClient client;

    public OpenAiService() {
        this.client = WebClient.create();
    }

    // Use this constructor for testing, to inject a mock client
    public OpenAiService(WebClient client) {
        this.client = client;
    }

    public MyResponse makeRequest(String userPrompt, String _systemMessage) {

        // Fetch data from the new API
        List<Event> events = fetchBettingData();

        // Format the events data
        String dataAsString = formatEventsForPrompt(events);

        // Combine the data with the original prompt
        String combinedPrompt = userPrompt + "\n\nHere is the latest betting data:\n" + dataAsString;

        // Create the OpenAI API request
        ChatCompletionRequest requestDto = new ChatCompletionRequest();
        requestDto.setModel(MODEL);
        requestDto.setTemperature(TEMPERATURE);
        requestDto.setMax_tokens(MAX_TOKENS);
        requestDto.setTop_p(TOP_P);
        requestDto.setFrequency_penalty(FREQUENCY_PENALTY);
        requestDto.setPresence_penalty(PRESENCE_PENALTY);
        requestDto.getMessages().add(new ChatCompletionRequest.Message("system", _systemMessage));
        requestDto.getMessages().add(new ChatCompletionRequest.Message("user", combinedPrompt));

        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        String err = null;
        try {
            json = mapper.writeValueAsString(requestDto);
            System.out.println(json);
            ChatCompletionResponse response = client.post()
                    .uri(new URI(URL))
                    .header("Authorization", "Bearer " + API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(json))
                    .retrieve()
                    .bodyToMono(ChatCompletionResponse.class)
                    .block();
            String responseMsg = response.getChoices().get(0).getMessage().getContent();
            int tokensUsed = response.getUsage().getTotal_tokens();
            System.out.print("Tokens used: " + tokensUsed);
            System.out.print(". Cost ($0.0015 / 1K tokens) : $" + String.format("%6f", (tokensUsed * 0.0015 / 1000)));
            System.out.println(". For 1$, this is the amount of similar requests you can make: " + Math.round(1 / (tokensUsed * 0.0015 / 1000)));
            return new MyResponse(responseMsg);
        } catch (WebClientResponseException e) {
            logger.error("Error response status code: " + e.getRawStatusCode());
            logger.error("Error response body: " + e.getResponseBodyAsString());
            logger.error("WebClientResponseException", e);
            err = "Internal Server Error, due to a failed request to external service. You could try again" +
                    "( While you develop, make sure to consult the detailed error message on your backend)";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, err);
        } catch (Exception e) {
            logger.error("Exception", e);
            err = "Internal Server Error - You could try again" +
                    "( While you develop, make sure to consult the detailed error message on your backend)";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, err);
        }
    }

    private List<Event> fetchBettingData() {
        try {
            Event[] eventsArray = client.get()
                    .uri(bettingApiUrl)
                    .header("Authorization", "Bearer " + bettingApiKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Event[].class)
                    .block();

            // Convert the array to a list and log the events
            List<Event> events = Arrays.asList(eventsArray);
            logger.debug("Fetched events: " + events);

            return events;
        } catch (WebClientResponseException e) {
            logger.error("Error fetching betting data: " + e.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch betting data");
        } catch (Exception e) {
            logger.error("Exception fetching betting data", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch betting data");
        }
    }



    private String formatEventsForPrompt(List<Event> events) {
        StringBuilder sb = new StringBuilder();
        for (Event event : events) {
            sb.append("Event ID: ").append(event.getId()).append("\n");
            sb.append("Sport: ").append(event.getSport_title()).append("\n");
            sb.append("Home Team: ").append(event.getHome_team()).append("\n");
            sb.append("Away Team: ").append(event.getAway_team()).append("\n");
            sb.append("Commence Time: ").append(event.getCommence_time()).append("\n");
            sb.append("Bookmakers:\n");
            for (Bookmaker bookmaker : event.getBookmakers()) {
                sb.append("  - ").append(bookmaker.getTitle()).append("\n");
                for (Market market : bookmaker.getMarkets()) {
                    sb.append("    Market: ").append(market.getKey()).append("\n");
                    for (Outcome outcome : market.getOutcomes()) {
                        sb.append("      Outcome: ").append(outcome.getName())
                                .append(", Price: ").append(outcome.getPrice()).append("\n");
                    }
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
