package com.ranks.configuration;

import com.ranks.calculation.StrongmanItemProcessor;
import com.ranks.model.Rank;
import com.ranks.model.Strongman;
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
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@Import({PropertyConfiguration.class, DBConfiguration.class})
public class BatchConfiguration {

    @Bean
    @Autowired
    public ItemReader<Strongman> reader(DataSource dataSource) {
        JdbcCursorItemReader<Strongman> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT userId, parameterValue FROM public.StrengthInfo;");
        reader.setRowMapper((rs, rowNumb) -> {
            Strongman strongman = new Strongman();
            strongman.setName(rs.getString("userId"));
            strongman.setBenchPress(rs.getInt("parameterValue"));
            return strongman;
        });

        return reader;
    }

    @Bean
    public ItemProcessor<Strongman, Rank> processor() {
        return new StrongmanItemProcessor();
    }

    @Bean
    public ItemWriter<Rank> writer(@Value("${module.outputResource}") String outputResource) {
        FlatFileItemWriter<Rank> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(outputResource));
        writer.setLineAggregator(new PassThroughLineAggregator<>());
        return writer;
    }

    @Bean
    public Job createStrongmenJob(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("strongmenJob")
                .flow(step).end().build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, ItemReader<Strongman> reader,
                     ItemWriter<Rank> writer, ItemProcessor<Strongman, Rank> processor) {
        return stepBuilderFactory.get("step")
                .<Strongman, Rank>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
