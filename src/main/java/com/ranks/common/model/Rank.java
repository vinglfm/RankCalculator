package com.ranks.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Rank {
    private final String userId;
    private final LocalDate measurementDate;
    private final long rank;
    private final int position;
}
