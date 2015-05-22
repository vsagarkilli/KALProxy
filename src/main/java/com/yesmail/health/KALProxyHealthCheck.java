package com.yesmail.health;

import database.DataSource;
import mongo.MongoConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class KALProxyHealthCheck implements HealthIndicator {

    @Autowired
    private MongoConnection.MConnect mongoDataSoure;
    @Autowired
    private DataSource dataSource;

    @Override
    public Health health() {
        int errorCode = check(); // perform some specific health check
        if (errorCode != 0) {
            return Health.down().withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();
    }

    private int check(){

        if(mongoDataSoure==null)
            return -1;

        try{
            mongoDataSoure.listProjects();
        }catch(Exception e){
            return -1;
        }

        if(dataSource==null || dataSource.database==null)
            return -1;

        return 0;
    }
}