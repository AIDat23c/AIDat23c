package com.example.aidat23c.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BetRequest {


    private int amountOfMatches;
    private int moneyReturned;
    private String league;
    private String bookmaker;
    private String userInput;
}
