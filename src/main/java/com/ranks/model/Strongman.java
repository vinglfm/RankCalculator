package com.ranks.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
public class Strongman {
    private String name;
    private int rank;
    private int benchPress;
}
