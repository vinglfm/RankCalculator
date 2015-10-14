package com.ranks.batch.calculator;

import com.ranks.common.model.Body;
import org.springframework.stereotype.Component;

@Component
public class BaseRankCalculator implements RankCalculator {

    @Override
    public long calculate (Body body) {
        return Math.round(body.getAnkle() + body.getBiceps() + body.getChest());
    }
}
