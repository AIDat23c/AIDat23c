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
    @JsonProperty("id")
    private String id;

    @JsonProperty("sport_key")
    private String sportKey;

    @JsonProperty("sport_title")
    private String sportTitle;

    @JsonProperty("commence_time")
    private String commenceTime;

    @JsonProperty("home_team")
    private String homeTeam;

    @JsonProperty("away_team")
    private String awayTeam;

    @JsonProperty("bookmakers")
    private List<Bookmaker> bookmakers;


    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", sportKey='" + sportKey + '\'' +
                ", sportTitle='" + sportTitle + '\'' +
                ", commenceTime='" + commenceTime + '\'' +
                ", homeTeam='" + homeTeam + '\'' +
                ", awayTeam='" + awayTeam + '\'' +
                ", bookmakers=" + bookmakers +
                '}';
    }
}
