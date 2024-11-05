package com.example.aidat23c.api;

import com.example.aidat23c.dtos.MyResponse;
import com.example.aidat23c.service.OpenAiService;
import org.springframework.web.bind.annotation.*;

/**
 * This class handles fetching a joke via the ChatGPT API
 */
@RestController
@RequestMapping("/api/v1/joke")
@CrossOrigin(origins = "*")
public class BetController {

    private final OpenAiService service;

    /**
     * This contains the message to the ChatGPT API, telling the AI how it should act in regard to the requests it gets.
     */
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
     * The controller called from the browser client.
     * @param service
     */
    public BetController(OpenAiService service) {
        this.service = service;
    }

    /**
     * Handles the request from the browser client.
     * @param about contains the input that ChatGPT uses to make a joke about.
     * @return the response from ChatGPT.
     */
    @GetMapping
    public MyResponse getJoke(@RequestParam String about) {

        return service.makeRequest(about,SYSTEM_MESSAGE);
    }
}