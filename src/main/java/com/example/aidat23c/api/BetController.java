package com.example.aidat23c.api;

import com.example.aidat23c.dtos.*;
import com.example.aidat23c.service.BettingApiService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/openai")
public class BetController {

    private final BettingApiService bettingApiService;

    @Value("${app.bucket_capacity}")
    private int BUCKET_CAPACITY;

    @Value("${app.refill_amount}")
    private int REFFILL_AMOUNT;

    @Value("${app.refill_time}")
    private int REFILL_TIME;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();


    public BetController(BettingApiService bettingApiService) {
        this.bettingApiService = bettingApiService;
    }
    final static String SYSTEM_MESSAGE = "You are a professional betting instructor. You will be presented with a JSON " +
            "file consisting of football/soccer matches, this will also include the bookmakers odds for each match, the odds can be h2h and totals. " +
            "You will be given an amount of games the user wants to bet on and you will choose which are worth betting on based on the teams' last 5 games and their performance and return your answer. " +
            "You will also be given an amount the user wants to earn back from the bet, this will be an estimate as you won't have to find the exact amount, but be in the range of <5. If that's not possible remove a match from the bet and dont include it in your printed answer" +
            "The user also has the option to give you an extra request, though it is not necessary for them to do so. If the request is not about the bet, please ignore the request. " +
            "Please give the answer in a short and simple format for the reader and display the league and bookmaker and the combined odds. Be precise by multiplying all selected odds together and rounding only at the end." +
            "You will use emojis." +
            "\"When calculating combined odds for betting selections, multiply the odds of each selected match exactly, without rounding intermediate results, and only round to two decimal places at the end.\" " +
            "The format of the data you're getting will be like this:\n" +
            "{\n" +
            "    \"id\": <value>,\n" +
            "    \"home_team\": <value>,\n" +
            "    \"away_team\": <value>,\n" +
            "    \"bookmakers\": [\n" +
            "        {\n" +
            "            \"key\": <value>,\n" +
            "            \"markets\": [\n" +
            "                {\n" +
            "                    \"outcomes\": [\n" +
            "                        {\n" +
            "                            \"name\": <value>,\n" +
            "                            \"price\": <value>\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"name\": <value>,\n" +
            "                            \"price\": <value>\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"name\": <value>,\n" +
            "                            \"price\": <value>\n" +
            "                        }\n" +
            "                    ]\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"key\": <value>,\n" +
            "                {\n" +
            "                    \"outcomes\": [\n" +
            "                        {\n" +
            "                            \"name\": <value>,\n" +
            "                            \"price\": <value>\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"name\": <value>,\n" +
            "                            \"price\": <value>\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"name\": <value>,\n" +
            "                            \"price\": <value>\n" +
            "                        }\n" +
            "                    ]\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}";


    @PostMapping("/generate")
    public MyResponse generateResponse(@RequestBody BetRequest betRequest, HttpServletRequest request) {
        System.out.println("Received user prompt: " + betRequest.getUserInput());

        String ip = request.getRemoteAddr();
        Bucket bucket = getBucket(ip);
        if (!bucket.tryConsume(1)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests, try again later");
        }

        return bettingApiService.generateBettingAdvice(betRequest, SYSTEM_MESSAGE);
    }
    @PostMapping("/generate-random")
    public MyResponse generateRandomResponse() {
        return bettingApiService.generateRandomBet(SYSTEM_MESSAGE);
    }



    private Bucket createNewBucket(){
        Bandwidth limit = Bandwidth.classic(BUCKET_CAPACITY, Refill.greedy(REFFILL_AMOUNT, Duration.ofMinutes(REFILL_TIME)));
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket getBucket(String key) {
        return buckets.computeIfAbsent(key, k -> createNewBucket());
    }



    @GetMapping("/health")
    public String healthCheck() {
        return "OpenAI Betting Assistant Service is running.";
    }

    @GetMapping("/leagues")
    public List<League> getLeagues() {
        return bettingApiService.fetchFilteredLeagues();
    }

    @GetMapping("/bookmakers/{leagueId}")
    public List<Bookmaker> getBookmakers(@PathVariable String leagueId) {
        return bettingApiService.fetchFilteredBookmakers(leagueId);
    }


}
