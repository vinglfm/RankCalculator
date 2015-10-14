package com.ranks.calculator.configuration;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.ranks.calculator")
public class SparkConfiguration {

    @Bean
    public SparkConf sparkConf (@Value("${module.name}") String appName) {
        return new SparkConf().setAppName(appName).setMaster("local[*]");
    }

    @Bean
    @Autowired
    public JavaSparkContext javaSparkContext (SparkConf sparkConf) {
        return new JavaSparkContext(sparkConf);
    }

    @Bean
    @Autowired
    public SQLContext sqlContext (JavaSparkContext javaSparkContext) {
        return new SQLContext(javaSparkContext);
    }
}
