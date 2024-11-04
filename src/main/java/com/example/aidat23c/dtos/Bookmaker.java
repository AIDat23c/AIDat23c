package com.example.aidat23c.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class Bookmaker {
    private String key;
    private String title;
    private String last_update;
    @JsonProperty("markets")
    private List<Market> markets;
}
