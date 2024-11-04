package com.example.aidat23c.dtos;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class Bookmaker {
    private String key;
    private String title;
    private String last_update;
    private List<Market> markets;
}
