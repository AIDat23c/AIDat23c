package com.example.aidat23c.api;

import com.example.aidat23c.dtos.*;
import com.example.aidat23c.service.OpenAiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/openai")
public class OpenAiController {

    private final OpenAiService openAiService;

    public OpenAiController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }
    final static String SYSTEM_MESSAGE = "You are a professional betting instructor. You will be presented with a JSON " +
            "file consisting of football/soccer matches, this will also include the bookmakers odds for each match. " +
                "You will choose 3 matches you think are worth betting on based on the teams last 5 games and their performance and return your answer. " +
            "The format of the data you're getting will be like this:\n" +
            "{\n" +
            "    \"id\": <value>,\n" +
            "    \"home_team\": <value>,\n" +
            "    \"away_team\": <value>,\n" +
            "    \"bookmakers\": [\n" +
            "      {\n" +
            "        \"key\": <value>\n" +
            "        \"markets\": [\n" +
            "          {\n" +
            "            \"outcomes\": [\n" +
            "              {\n" +
            "                \"name\": <value>,\n" +
            "                \"price\": <value>\n" +
            "              },\n" +
            "              {\n" +
            "                \"name\": <value>,\n" +
            "                \"price\": <value>\n" +
            "              },\n" +
            "              {\n" +
            "                \"name\": <value>,\n" +
            "                \"price\": <value>\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": <value>\n" +
            "        \"markets\": [\n" +
            "          {\n" +
            "            \"outcomes\": [\n" +
            "              {\n" +
            "                \"name\": <value>,\n" +
            "                \"price\": <value>\n" +
            "              },\n" +
            "              {\n" +
            "                 \"name\": <value>,\n" +
            "                \"price\": <value>\n" +
            "              },\n" +
            "              {\n" +
            "                 \"name\": <value>,\n" +
            "                \"price\": <value>\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ]\n" +
            "  }";

    @PostMapping("/generate")
    public MyResponse generateResponse(@RequestBody BetRequest betRequest) {
        return openAiService.generateBettingAdvice(betRequest, SYSTEM_MESSAGE);
    }

   /* @GetMapping("/generate")
    public ResponseEntity<MyResponse> generateBettingAdvice(
            @RequestParam int amountOfMatches,
            @RequestParam int moneyReturned) {

        // Optionally, you can include a system message or set a default one
        String systemMessage = "Your default system message here";

        MyResponse response = openAiService.generateBettingAdvice(amountOfMatches, moneyReturned, systemMessage);
        return ResponseEntity.ok(response);
    }*/
    /**
     * Health check endpoint to verify if the service is up and running.
     *
     * @return A simple message confirming the service is running.
     */
    @GetMapping("/health")
    public String healthCheck() {
        return "OpenAI Betting Assistant Service is running.";
    }

    // Endpoint to get filtered leagues
    @GetMapping("/leagues")
    public List<League> getLeagues() {
        return openAiService.fetchFilteredLeagues();
    }

    // Endpoint to get filtered bookmakers for a selected league
    @GetMapping("/bookmakers/{leagueId}")
    public List<Bookmaker> getBookmakers(@PathVariable String leagueId) {
        return openAiService.fetchFilteredBookmakers(leagueId);
    }
}
