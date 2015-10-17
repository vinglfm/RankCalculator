package com.ranks.batch.processor;

import com.ranks.batch.calculator.RankCalculator;
import com.ranks.common.model.Body;
import com.ranks.common.model.Rank;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class BodyItemProcessor implements ItemProcessor<Body, Rank> {

    @Autowired
    private RankCalculator rankCalculator;

    @Override
    public Rank process (Body strongman) throws Exception {
        return Rank.builder().userId(strongman.getUserId()).measurementDate(strongman.getMeasurementDate())
                .rank(rankCalculator.calculate(strongman)).build();
    }
}
