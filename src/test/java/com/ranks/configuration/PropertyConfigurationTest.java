package com.ranks.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = PropertyConfiguration.class)
public class PropertyConfigurationTest extends AbstractTestNGSpringContextTests {

    @Value("${module.name}")
    private String module;

    @Test
    public void moduleNameShouldBeConfigured() {
        assertThat(module).isNotEmpty().isEqualTo("RankCalculator");
    }
}