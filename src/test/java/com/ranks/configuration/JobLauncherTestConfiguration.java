package com.ranks.configuration;

import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(BatchConfiguration.class)
public class JobLauncherTestConfiguration {

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        //TODO: require manual setup in case of few jobs in the configuration
        return new JobLauncherTestUtils();
    }
}
