package com.ranks.batch.configuration;

import com.ranks.batch.scheduler.RankCalculationSchedulerConfiguration;
import org.junit.Test;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

public class RankCalculationSchedulerConfigurationTest extends AbstractTestNGSpringContextTests {

    @Test
    public void startRankCalculationScheduler () throws SchedulerException, InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(RankCalculationSchedulerConfiguration.class);
        Thread.sleep(5 * 60 * 1000);
    }

}