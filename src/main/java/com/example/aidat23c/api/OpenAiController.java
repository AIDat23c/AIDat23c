package com.example.aidat23c.api;

import com.example.aidat23c.dtos.MyResponse;
import com.example.aidat23c.service.OpenAiService;
import org.springframework.web.bind.annotation.*;

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
    /**
     * Endpoint to generate betting advice based on the user prompt and latest betting data.
     *
     * @param userPrompt A prompt from the user describing the kind of betting advice they need.
     * @return MyResponse containing the AI-generated advice.
     */
    @PostMapping("/generate")
    public MyResponse generateResponse(@RequestParam String userPrompt) {
        return openAiService.generateBettingAdvice(userPrompt, SYSTEM_MESSAGE);
    }

    /**
     * Health check endpoint to verify if the service is up and running.
     *
     * @return A simple message confirming the service is running.
     */
    @GetMapping("/health")
    public String healthCheck() {
        return "OpenAI Betting Assistant Service is running.";
    }
}
