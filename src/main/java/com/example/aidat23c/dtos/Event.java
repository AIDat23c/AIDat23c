package com.example.aidat23c.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class Event {
    @JsonProperty("id")
    private String id;
    @JsonProperty("sport_key")
    private String sport_key;
    @JsonProperty("sport_title")
    private String sport_title;
    private String commence_time;
    private String home_team;
    private String away_team;
    @JsonProperty("bookmakers")
    private List<Bookmaker> bookmakers;
}
