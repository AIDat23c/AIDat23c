package com.example.aidat23c.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class Market {
    @JsonProperty("key")
    private String key;

    @JsonProperty("last_update")
    private String lastUpdate;

    @JsonProperty("outcomes")
    private List<Outcome> outcomes;

    // Getters and Setters
}