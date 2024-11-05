package com.example.aidat23c.service;

import com.example.aidat23c.dtos.*;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpenAiService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiService.class);

    @Value("${app.api-key}")
    private String apiKey;

    @Value("${app.url}")
    private String apiUrl;

    @Value("${app.model}")
    private String model;

    @Value("${app.temperature}")
    private double temperature;

    @Value("${app.max_tokens}")
    private int maxTokens;

    @Value("${app.frequency_penalty}")
    private double frequencyPenalty;

    @Value("${app.presence_penalty}")
    private double presencePenalty;

    @Value("${app.top_p}")
    private double topP;

    @Value("${app.betting-api-url}")
    private String bettingApiUrl;

    @Value("${app.betting-api-key}")
    private String bettingApiKey;

    private final WebClient client;

    public OpenAiService(WebClient.Builder webClientBuilder) {
        this.client = webClientBuilder.build();
    }

    public MyResponse generateBettingAdvice(int amountOfMatches,int moneyReturned,String systemMessage) {

        // Fetch data from the betting API
        List<Event> events = fetchBettingData();

        // Format the events data
        String dataAsString = formatEventsForPrompt(events);

        // Combine the data with the user prompt
        String combinedPrompt =  "Here is the latest betting data:\n" + dataAsString + "\n the user requests that the bet includes " + amountOfMatches
                +" matches" + "\n and the user wants their money returned " + moneyReturned + " times";

        // Create and send the OpenAI API request
        ChatCompletionRequest requestDto = new ChatCompletionRequest();
        requestDto.setModel(model);
        requestDto.setTemperature(temperature);
        requestDto.setMax_tokens(maxTokens);
        requestDto.setTop_p(topP);
        requestDto.setFrequency_penalty(frequencyPenalty);
        requestDto.setPresence_penalty(presencePenalty);
        requestDto.getMessages().add(new ChatCompletionRequest.Message("system", systemMessage));
        requestDto.getMessages().add(new ChatCompletionRequest.Message("user", combinedPrompt));

        try {
            ChatCompletionResponse response = client.post()
                    .uri(new URI(apiUrl))
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestDto))
                    .retrieve()
                    .bodyToMono(ChatCompletionResponse.class)
                    .block();

            String responseMsg = response.getChoices().get(0).getMessage().getContent();
            int tokensUsed = response.getUsage().getTotal_tokens();
            System.out.print("Tokens used: " + tokensUsed);
            System.out.print(". Cost ($0.0015 / 1K tokens) : $" + String.format("%6f",(tokensUsed * 0.0015 / 1000)));
            System.out.println(". For 1$, this is the amount of similar requests you can make: " + Math.round(1/(tokensUsed * 0.0015 / 1000)));
            return new MyResponse(responseMsg);

        } catch (WebClientResponseException e) {
            logger.error("Error response from OpenAI API: " + e.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get response from OpenAI");
        } catch (Exception e) {
            logger.error("Exception", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process betting data");
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
            sb.append("Sports key: ").append(event.getSportKey()).append("\n");
            sb.append("Home Team: ").append(event.getHomeTeam()).append("\n");
            sb.append("Away Team: ").append(event.getAwayTeam()).append("\n");
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

    public List<League> fetchFilteredLeagues() {
        try {
            URI uri = UriComponentsBuilder.fromUriString(bettingApiUrl + "/sports")
                    .queryParam("apiKey", bettingApiKey)
                    .build()
                    .toUri();

            League[] leagues = client.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(League[].class)
                    .block();

            return Arrays.stream(leagues)
                    .filter(league -> league.getKey().startsWith("soccer"))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error fetching filtered leagues", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch leagues");
        }
    }

    public List<Bookmaker> fetchFilteredBookmakers(String leagueId) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(bettingApiUrl + "/sports/" + leagueId + "/odds")
                    .queryParam("regions", "eu")
                    .queryParam("markets", "h2h")
                    .queryParam("apiKey", bettingApiKey)
                    .build()
                    .toUri();

            Event[] events = client.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Event[].class)
                    .block();

            return Arrays.stream(events)
                    .flatMap(event -> event.getBookmakers().stream())
                    .distinct()
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error fetching bookmakers for league: " + leagueId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch bookmakers");
        }
    }


}
