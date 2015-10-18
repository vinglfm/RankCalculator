package com.ranks.batch.helper;

import com.ranks.batch.jobs.RankCalculationBatchJob;
import com.ranks.batch.scheduler.RankCalculationSchedulerConfiguration;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
//@Import({RankCalculationSchedulerConfiguration.class})
public class JobLauncherTestConfiguration {

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils () {
        //TODO: require manual setup in case of few jobs in the configuration
        return new JobLauncherTestUtils();
    }
}
