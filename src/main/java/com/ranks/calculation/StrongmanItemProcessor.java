package com.ranks.calculation;

import com.ranks.model.Rank;
import com.ranks.model.Strongman;
import org.springframework.batch.item.ItemProcessor;


public class StrongmanItemProcessor implements ItemProcessor<Strongman, Rank> {

    @Override
    public Rank process(Strongman strongman) throws Exception {
        return new Rank(strongman.getName(), 1);
    }
}
