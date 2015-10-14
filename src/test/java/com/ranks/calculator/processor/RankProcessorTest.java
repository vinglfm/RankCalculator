package com.ranks.calculator.processor;

import com.ranks.batch.configuration.PropertyConfiguration;
import com.ranks.calculator.configuration.SparkConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(classes = {SparkConfiguration.class, PropertyConfiguration.class})
public class RankProcessorTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private RankProcessor rankProcessor;

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void processShouldReadDataFromDatabase () {
        rankProcessor.process();
    }
}