package com.example.aidat23c.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Outcome {
    @JsonProperty("name")
    private String name;

    @JsonProperty("price")
    private double price;

}