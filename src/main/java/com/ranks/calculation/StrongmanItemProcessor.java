package com.ranks.calculation;

import com.ranks.model.Rank;
import com.ranks.model.Strongman;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class StrongmanItemProcessor implements ItemProcessor<Strongman, Rank> {

    private static final String RANK_QUERY = "SELECT rankName FROM public.StrongmenRank WHERE rankLow <= ? AND rankHigh >= ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Rank process(Strongman strongman) throws Exception {
        String rank = jdbcTemplate.queryForObject(RANK_QUERY,
                new Object[]{strongman.getBenchPress(), strongman.getBenchPress()},
                String.class);
        return new Rank(strongman.getName(), rank);
    }
}
