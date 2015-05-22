package com.yesmail.config;

import database.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
@ComponentScan("com.yesmail")
public class DatabaseConfig {

    @Value("${db.mysql.properties}")
    private String dataSourceConnectionString;

    @Bean
    public DataSource dataSource(){
        DataSource dataSource = new DataSource(dataSourceConnectionString);
        if (dataSource == null) {
            throw new RuntimeException("Unable to get Database connection");
        }
        return dataSource;
    }
}
