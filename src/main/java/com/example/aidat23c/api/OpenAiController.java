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

    /**
     * Endpoint to generate betting advice based on the user prompt and latest betting data.
     *
     * @param userPrompt A prompt from the user describing the kind of betting advice they need.
     * @return MyResponse containing the AI-generated advice.
     */
    @PostMapping("/generate")
    public MyResponse generateResponse(@RequestParam String userPrompt) {
        return openAiService.generateBettingAdvice(userPrompt);
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
