package com.ranks.batch.scheduler;

import com.google.common.collect.ImmutableMap;
import com.ranks.batch.jobs.RankCalculationBatchJob;
import org.quartz.JobDataMap;
import org.quartz.TriggerBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerKey.triggerKey;

@Configuration
@ComponentScan("com.ranks.batch.scheduler")
@Import(RankCalculationBatchJob.class)
public class RankCalculationSchedulerConfiguration {

    @Bean
    public ResourcelessTransactionManager transactionManager () {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public ApplicationContextFactory someJobs () {
        return new GenericApplicationContextFactory(RankScheduler.class);
    }

    @Bean
    @Autowired
    public JobRepositoryFactoryBean jobRepositoryFactoryBean (DataSource dataSource, PlatformTransactionManager transactionManager) {
        JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setDataSource(dataSource);
        jobRepositoryFactoryBean.setTransactionManager(transactionManager);
        jobRepositoryFactoryBean.setDatabaseType("postgres");
        return jobRepositoryFactoryBean;
    }

    @Bean
    public JobBuilderFactory jobBuilderFactory (JobRepositoryFactoryBean jobRepositoryFactory) throws Exception {
        return new JobBuilderFactory(jobRepositoryFactory.getObject());
    }

    @Bean
    public StepBuilderFactory stepBuilderFactory (JobRepositoryFactoryBean jobRepositoryFactory,
                                                  PlatformTransactionManager transactionManager) throws Exception {
        return new StepBuilderFactory(jobRepositoryFactory.getObject(), transactionManager);
    }

    @Bean
    @Autowired
    public JobLauncher jobLauncher (JobRepositoryFactoryBean jobRepository) throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository.getObject());
        return jobLauncher;
    }

    @Bean
    public JobRegistry jobRegistry () {
        return new MapJobRegistry();
    }

    @Bean
    @Autowired
    public DefaultJobLoader defaultJobLoader (JobRegistry jobRegistry) {
        return new DefaultJobLoader(jobRegistry);
    }

    @Bean
    @Autowired
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor (JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    @Bean
    @Autowired
    public JobDetailFactoryBean jobDetailFactoryBean (JobLauncher jobLauncher, JobRegistry jobRegistry) {
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(RankScheduler.class);
        jobDetailFactoryBean.setJobDataAsMap(new JobDataMap(ImmutableMap.builder()
                .put("jobName", "rankCalculationJob")
                .put("jobLauncher", jobLauncher)
                .put("jobLocator", jobRegistry)
                .build()));
//        jobDetailFactoryBean.afterPropertiesSet();
        return jobDetailFactoryBean;
    }


    @Bean
    @Autowired
    public SchedulerFactoryBean schedulerFactoryBean (JobDetailFactoryBean jobDetailFactoryBean) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setTriggers(TriggerBuilder.newTrigger()
                .withIdentity(triggerKey("rankTrigger"))
                .usingJobData(new JobDataMap(
                        ImmutableMap.builder().put("jobDetail", jobDetailFactoryBean.getObject()).build()))
                .withSchedule(simpleSchedule().withIntervalInSeconds(120).withRepeatCount(0))
                .build());
        return schedulerFactoryBean;
    }

}
