package com.example.aidat23c.api;

import com.example.aidat23c.dtos.MyResponse;
import com.example.aidat23c.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/openai")
public class OpenAiController {

    private final OpenAiService openAiService;

    @Autowired
    public OpenAiController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping("/generate")
    public MyResponse generateResponse(@RequestParam String userPrompt, @RequestParam(required = false) String systemMessage) {
        if (systemMessage == null || systemMessage.isEmpty()) {
            systemMessage = "\"You are a professional betting instructor. You will be presented with a JSON \" +\n" +
                    "            \"file consisting of football/soccer matches, this will also include the bookmakers odds for each match. \" +\n" +
                    "            \"You will choose 3 matches you think are worth betting on based on the teams last 5 games and their performance and return your answer. \" +\n" +
                    "            \"The format of the data you're getting will be like this:\\n\" +\n" +
                    "            \"{\\n\" +\n" +
                    "            \"    \\\"id\\\": <value>,\\n\" +\n" +
                    "            \"    \\\"home_team\\\": <value>,\\n\" +\n" +
                    "            \"    \\\"away_team\\\": <value>,\\n\" +\n" +
                    "            \"    \\\"bookmakers\\\": [\\n\" +\n" +
                    "            \"      {\\n\" +\n" +
                    "            \"        \\\"key\\\": <value>\\n\" +\n" +
                    "            \"        \\\"markets\\\": [\\n\" +\n" +
                    "            \"          {\\n\" +\n" +
                    "            \"            \\\"outcomes\\\": [\\n\" +\n" +
                    "            \"              {\\n\" +\n" +
                    "            \"                \\\"name\\\": <value>,\\n\" +\n" +
                    "            \"                \\\"price\\\": <value>\\n\" +\n" +
                    "            \"              },\\n\" +\n" +
                    "            \"              {\\n\" +\n" +
                    "            \"                \\\"name\\\": <value>,\\n\" +\n" +
                    "            \"                \\\"price\\\": <value>\\n\" +\n" +
                    "            \"              },\\n\" +\n" +
                    "            \"              {\\n\" +\n" +
                    "            \"                \\\"name\\\": <value>,\\n\" +\n" +
                    "            \"                \\\"price\\\": <value>\\n\" +\n" +
                    "            \"              }\\n\" +\n" +
                    "            \"            ]\\n\" +\n" +
                    "            \"          }\\n\" +\n" +
                    "            \"        ]\\n\" +\n" +
                    "            \"      },\\n\" +\n" +
                    "            \"      {\\n\" +\n" +
                    "            \"        \\\"key\\\": <value>\\n\" +\n" +
                    "            \"        \\\"markets\\\": [\\n\" +\n" +
                    "            \"          {\\n\" +\n" +
                    "            \"            \\\"outcomes\\\": [\\n\" +\n" +
                    "            \"              {\\n\" +\n" +
                    "            \"                \\\"name\\\": <value>,\\n\" +\n" +
                    "            \"                \\\"price\\\": <value>\\n\" +\n" +
                    "            \"              },\\n\" +\n" +
                    "            \"              {\\n\" +\n" +
                    "            \"                 \\\"name\\\": <value>,\\n\" +\n" +
                    "            \"                \\\"price\\\": <value>\\n\" +\n" +
                    "            \"              },\\n\" +\n" +
                    "            \"              {\\n\" +\n" +
                    "            \"                 \\\"name\\\": <value>,\\n\" +\n" +
                    "            \"                \\\"price\\\": <value>\\n\" +\n" +
                    "            \"              }\\n\" +\n" +
                    "            \"            ]\\n\" +\n" +
                    "            \"          }\\n\" +\n" +
                    "            \"        ]\\n\" +\n" +
                    "            \"      }\\n\" +\n" +
                    "            \"    ]\\n\" +\n" +
                    "            \"  }\";";
        }
        return openAiService.makeRequest(userPrompt, systemMessage);
    }
}
