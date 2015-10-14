package com.ranks.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Rank {
    private final String userId;
    private final LocalDate date;
    private final long rank;
}
