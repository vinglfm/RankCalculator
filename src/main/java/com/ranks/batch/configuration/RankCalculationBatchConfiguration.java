package com.ranks.batch.configuration;

import com.ranks.batch.processor.BodyItemProcessor;
import com.ranks.common.model.Body;
import com.ranks.common.model.Rank;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Configuration
@EnableBatchProcessing
@Import({PropertyConfiguration.class})
@ImportResource("classpath:dbConfig.xml")
public class RankCalculationBatchConfiguration {

    private static final String SELECT_LATEST_BODY_QUERY = "SELECT bodyInfo.userId, bodyInfo.measurementDate,\n" +
            " bodyInfo.neck, bodyInfo.chest,\n" +
            " bodyInfo.waist, bodyInfo.biceps,\n" +
            " bodyInfo.forearm, bodyInfo.wrist, bodyInfo.hip, bodyInfo.thigh, bodyInfo.gastrocnemius,\n" +
            " bodyInfo.ankle, bodyInfo.fatpercentage\n" +
            " FROM (SELECT userId, MAX(measurementDate) as maxDate FROM public.BodyInfo GROUP BY userId) innerQuery\n" +
            " INNER JOIN public.BodyInfo bodyInfo ON innerQuery.userId = bodyInfo.userId AND innerQuery.maxDate = bodyInfo.measurementDate;\n";
    private static final String INSERT_RANK_QUERY = "INSERT INTO BodyRank(userId, measurementDate, rank) VALUES(?, ?," +
            " ?);";
    //TODO: rank duplication low position issue
    private static final String SELECT_BODY_POSITION_QUERY = "SELECT userId, measurementDate,\n" +
            " row_number() over (order by rank DESC nulls last) as position FROM BodyRank;";
    private static final String UPDATE_BODY_POSITION_QUERY = "UPDATE BodyRank SET position = ? WHERE userId = ? AND " +
            "measurementDate = ?;";

    @Bean
    @Autowired
    public ItemReader<Body> bodyReader (DataSource dataSource) {
        JdbcCursorItemReader<Body> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(SELECT_LATEST_BODY_QUERY);
        reader.setRowMapper((resultSet, rowNumb) ->
                        Body.builder()
                                .userId(resultSet.getString("userId"))
                                .measurementDate(resultSet.getDate("measurementDate").toLocalDate())
                                .neck(resultSet.getDouble("neck"))
                                .chest(resultSet.getDouble("chest"))
                                .waist(resultSet.getDouble("waist"))
                                .biceps(resultSet.getDouble("biceps"))
                                .forearm(resultSet.getDouble("forearm"))
                                .wrist(resultSet.getDouble("wrist"))
                                .hip(resultSet.getDouble("hip"))
                                .thigh(resultSet.getDouble("thigh"))
                                .gastrocnemius(resultSet.getDouble("gastrocnemius"))
                                .ankle(resultSet.getDouble("ankle"))
                                .fatPercentage(resultSet.getInt("fatPercentage"))
                                .build()
        );

        return reader;
    }

    @Bean
    public ItemProcessor<Body, Rank> rankProcessor () {
        return new BodyItemProcessor();
    }

    @Bean
    @Autowired
    public ItemWriter<Rank> rankWriter (DataSource dataSource) {
        JdbcBatchItemWriter<Rank> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(INSERT_RANK_QUERY);
//        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setItemPreparedStatementSetter(new ItemPreparedStatementSetter<Rank>() {
            @Override
            public void setValues (Rank item, PreparedStatement ps) throws SQLException {
                ps.setString(1, item.getUserId());
                ps.setDate(2, Date.valueOf(item.getMeasurementDate()));
                ps.setLong(3, item.getRank());
            }
        });
        return writer;
    }

    @Bean
    @Autowired
    public ItemReader<Rank> positionReader (DataSource dataSource) {
        JdbcCursorItemReader<Rank> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(SELECT_BODY_POSITION_QUERY);
        reader.setRowMapper((resultSet, rowNumb) ->
                        Rank.builder()
                                .userId(resultSet.getString("userId"))
                                .measurementDate(resultSet.getDate("measurementDate").toLocalDate())
                                .position(resultSet.getInt("position"))
                                .build()
        );

        return reader;
    }

    @Bean
    @Autowired
    public ItemWriter<Rank> positionWriter (DataSource dataSource) {
        JdbcBatchItemWriter<Rank> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(UPDATE_BODY_POSITION_QUERY);
        writer.setItemPreparedStatementSetter((Rank item, PreparedStatement ps) -> {
                    ps.setInt(1, item.getPosition());
                    ps.setString(2, item.getUserId());
                    ps.setDate(3, Date.valueOf(item.getMeasurementDate()));
                }
        );
        return writer;
    }

    @Bean
    @Autowired
    public Step rankStep (StepBuilderFactory stepBuilderFactory,
                          @Qualifier("bodyReader") ItemReader<Body> reader,
                          @Qualifier("rankWriter") ItemWriter<Rank> writer, ItemProcessor<Body, Rank> processor,
                          @Value("${batch.chunkSize}") String chunkSize) {
        return stepBuilderFactory.get("rankStep")
                .<Body, Rank>chunk(Integer.parseInt(chunkSize))
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    @Autowired
    public Step positionStep (StepBuilderFactory stepBuilderFactory,
                              @Qualifier("positionReader") ItemReader<Rank> reader,
                              @Qualifier("positionWriter") ItemWriter<Rank> writer,
                              @Value("${batch.chunkSize}") String chunkSize) {
        return stepBuilderFactory.get("rankStep")
                .<Rank, Rank>chunk(Integer.parseInt(chunkSize))
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public Job rankCalculationJob (JobBuilderFactory jobBuilderFactory, Step rankStep, Step positionStep) {
        return jobBuilderFactory.get("rankCalculationJob")
                .incrementer(new RunIdIncrementer())
                .start(rankStep)
                .next(positionStep)
                .build();
    }
}
