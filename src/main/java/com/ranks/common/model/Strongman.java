package com.ranks.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Strongman implements Serializable {
    private String name;
    private double height;
    private double weight;
    private int fatPercentage;
    private double neck;
    private double chest;
    private double waist;
    private double biceps;
    private double forearm;
    private double wrist;
    private double hip;
    private double thigh;
    private double gastrocnemius;
    private double ankle;
    private int benchPress;
}
