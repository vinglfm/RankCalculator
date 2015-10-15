package com.ranks.common.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Body {
    private final String userId;
    private final LocalDate measurementDate;
    private final double neck;
    private final double chest;
    private final double waist;

    private final double biceps;
    private final double forearm;
    private final double wrist;

    private final double hip;
    private final double thigh;
    private final double gastrocnemius;
    private final double ankle;

    private final double height;
    private final double weight;
    private final int fatPercentage;
}
