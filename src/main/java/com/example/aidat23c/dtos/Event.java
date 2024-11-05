package com.example.aidat23c.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {


@JsonProperty("sport_key")
private String sportKey;
    @JsonProperty("home_team")
    private String homeTeam;

    @JsonProperty("away_team")
    private String awayTeam;

    @JsonProperty("bookmakers")
    private List<Bookmaker> bookmakers;


    @Override
    public String toString() {
        return "Event{" +
                ", sportskey='" + sportKey + '\'' +
                ", homeTeam='" + homeTeam + '\'' +
                ", awayTeam='" + awayTeam + '\'' +
                ", bookmakers=" + bookmakers +
                '}';
    }
}
