package com.yesmail.config;

import Environment.KAL;
import mongo.MongoConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.*;

import javax.xml.crypto.Data;

@Configuration
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
@ComponentScan("com.yesmail")
@Import({DatabaseConfig.class})
public class ApplicationConfig {

    @Value("${nosql.mongo.url}")
    private String mongoUrl;

    @Value("${nosql.mongo.store}")
    private String mongoDataStore;

    @Bean
    public MongoConnection.MConnect mongoDataSoure() throws Exception{
        //create/open a mongo database with configured datastore
        MongoConnection.MConnect mongo= null;
        mongo = MongoConnection.initialize(mongoUrl, mongoDataStore);
        if(mongo==null)
            throw new RuntimeException("Unable to instantiate mongo");
        return mongo;
    }

}
