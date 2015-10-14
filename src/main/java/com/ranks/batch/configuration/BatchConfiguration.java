package com.ranks.batch.configuration;

import com.ranks.batch.processor.BodyItemProcessor;
import com.ranks.common.model.Body;
import com.ranks.common.model.Rank;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@Import({PropertyConfiguration.class})
@ImportResource("classpath:dbConfig.xml")
public class BatchConfiguration {

    private static final String LATEST_BODY_INFO_QUERY = "SELECT bodyInfo.userId, bodyInfo.neck, bodyInfo.chest,\n" +
            " bodyInfo.waist, bodyInfo.biceps,\n" +
            " bodyInfo.forearm, bodyInfo.wrist, bodyInfo.hip, bodyInfo.thigh, bodyInfo.gastrocnemius,\n" +
            " bodyInfo.ankle, bodyInfo.fatpercentage\n" +
            " FROM (SELECT userId, MAX(measurementDate) as maxDate FROM public.BodyInfo GROUP BY userId) innerQuery\n" +
            " INNER JOIN public.BodyInfo bodyInfo ON innerQuery.userId = bodyInfo.userId AND innerQuery.maxDate = bodyInfo.measurementDate;\n";

    @Bean
    @Autowired
    public ItemReader<Body> reader (DataSource dataSource) {
        JdbcCursorItemReader<Body> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(LATEST_BODY_INFO_QUERY);
        reader.setRowMapper((resultSet, rowNumb) -> {
            final Body body = Body.builder()
                    .userId(resultSet.getString("userId"))
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
                    .build();
            return body;
        });

        return reader;
    }

    @Bean
    public ItemProcessor<Body, Rank> processor () {
        return new BodyItemProcessor();
    }

    @Bean
    public ItemWriter<Rank> writer (@Value("${module.outputResource}") String outputResource) {
        FlatFileItemWriter<Rank> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(outputResource));
        writer.setLineAggregator(new PassThroughLineAggregator<>());

//        DelimitedLineAggregator<Rank> lineAggregator = new DelimitedLineAggregator<>();
//        lineAggregator.setDelimiter(",");
//        BeanWrapperFieldExtractor<Rank> fieldExtractor = new BeanWrapperFieldExtractor<>();
//        fieldExtractor.setNames(new String[]{"userId", "date", "rank"});
//        lineAggregator.setFieldExtractor(fieldExtractor);
//        writer.setLineAggregator(lineAggregator);

        return writer;
    }

    @Bean
    public Job createStrongmenJob (JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("strongmenJob")
                .flow(step).end().build();
    }

    @Bean
    public Step step (StepBuilderFactory stepBuilderFactory, ItemReader<Body> reader,
                      ItemWriter<Rank> writer, ItemProcessor<Body, Rank> processor,
                      @Value("${batch.chunkSize}") String chunkSize) {
        return stepBuilderFactory.get("step")
                .<Body, Rank>chunk(Integer.parseInt(chunkSize))
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
