package com.example.aidat23c.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class League {
    @JsonProperty("key")
    private String key;

    @JsonProperty("title")
    private String title;

}
