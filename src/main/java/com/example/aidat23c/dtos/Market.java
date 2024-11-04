package com.example.aidat23c.dtos;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class Market {
    private String key;
    private String last_update;
    private List<Outcome> outcomes;
}
