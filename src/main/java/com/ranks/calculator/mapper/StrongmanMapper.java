package com.ranks.calculator.mapper;

import com.ranks.common.model.Strongman;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;

@Component
public class StrongmanMapper implements Function<Row, Strongman>, Serializable {

    private static final int NAME = 0;
    private static final int NECK = 2;
    private static final int CHEST = 3;
    private static final int WAIST = 4;
    private static final int BICEPS = 5;
    private static final int FOREARM = 6;
    private static final int WRIST = 7;
    private static final int HIP = 8;
    private static final int THIGH = 9;
    private static final int GASTROCNEMIUS = 10;
    private static final int ANKLE = 11;
    private static final int HEIGHT = 12;
    private static final int WEIGHT = 13;
    private static final int FAT_PERCENTAGE = 14;

    @Override
    public Strongman call (Row row) {
        Strongman strongman = new Strongman();

        strongman.setName(row.getString(NAME));
        strongman.setNeck(getDouble(row, NECK));
        strongman.setChest(getDouble(row, CHEST));
        strongman.setWaist(getDouble(row, WAIST));
        strongman.setBiceps(getDouble(row, BICEPS));
        strongman.setForearm(getDouble(row, FOREARM));
        strongman.setWrist(getDouble(row, WRIST));
        strongman.setHip(getDouble(row, HIP));
        strongman.setThigh(getDouble(row, THIGH));
        strongman.setGastrocnemius(getDouble(row, GASTROCNEMIUS));
        strongman.setAnkle(getDouble(row, ANKLE));
        strongman.setHeight(getDouble(row, HEIGHT));
        strongman.setWeight(getDouble(row, WEIGHT));
        strongman.setFatPercentage(row.getInt(FAT_PERCENTAGE));

        return strongman;
    }

    private double getDouble (Row row, int pos) {
        return ((BigDecimal) row.get(pos)).doubleValue();
    }
}
