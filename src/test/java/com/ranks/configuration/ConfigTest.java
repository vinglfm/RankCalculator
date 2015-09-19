package com.ranks.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = Config.class)
public class ConfigTest extends AbstractTestNGSpringContextTests {
    @Value("${module.name}")String module;

    @Test
    public void moduleNameShouldBeConfigured() {
        assertThat(module).isNotEmpty().isEqualTo("RankCalculator");
    }
}