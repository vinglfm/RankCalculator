package com.ranks.calculator.processor;

import com.google.common.collect.ImmutableMap;
import com.ranks.calculator.mapper.StrongmanMapper;
import com.ranks.common.model.Strongman;
import org.apache.spark.sql.SQLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RankProcessor implements Processor<Strongman> {

    private static final String URL = "url";
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String DBTABLE = "dbtable";

    @Autowired
    private SQLContext sqlContext;

    @Value("${db.url}")
    private String url;

    @Value("${db.user}")
    private String user;

    @Value("${db.password}")
    private String password;

    @Value("${db.table}")
    private String table;

    @Autowired
    private StrongmanMapper mapper;

    @Override
    public Strongman process () {

        List<Strongman> strongmen = sqlContext.read().format("jdbc").options(
                ImmutableMap.<String, String>builder()
                        .put(URL, url)
                        .put(USER, user)
                        .put(PASSWORD, password)
                        .put(DBTABLE, table)
                        .build()
        )
                .load().toJavaRDD()
                .map(mapper)
                .collect();

        strongmen.stream().forEach(strongman -> System.out.println(strongman));

//        dataFrame.collectAsList()
//                .stream().forEach(elem -> System.out.println("hey " + elem.getString(0)));
        throw new UnsupportedOperationException();
    }

}
