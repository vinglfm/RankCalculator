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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
@Import(PropertyConfiguration.class)
public class BatchConfiguration {

    @Bean
    public ItemReader<Strongman> reader(@Value("${module.inputResource}") String inputResource) {
        FlatFileItemReader<Strongman> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource(inputResource));
        reader.setLineMapper(new DefaultLineMapper<Strongman>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[]{"name"});
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Strongman>() {
                    {
                        setTargetType(Strongman.class);
                    }
                });
            }
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
                .<Strongman, Rank>chunk(5)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
