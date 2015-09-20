package com.ranks.calculation;

import com.ranks.model.Strongman;
import org.springframework.batch.item.ItemProcessor;


public class StrongmanItemProcessor implements ItemProcessor<Strongman, Strongman> {

    @Override
    public Strongman process(Strongman strongman) throws Exception {
        strongman.setBenchPress(10);
        strongman.setRank(10);
        return strongman;
    }
}
