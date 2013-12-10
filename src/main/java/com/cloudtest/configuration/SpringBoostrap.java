package com.cloudtest.configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: ysokolovski
 * Date: 26/11/13
 * Time: 1:14 PM
 */
@Configuration
public class SpringBoostrap {

    private @Autowired AWSCredentialsProvider securityProvider;

    @Bean
    public AWSCredentialsProvider securityCredentials() {
        return new ClasspathPropertiesFileCredentialsProvider();
    }

    @Bean
    public AmazonDynamoDBClient dynamoDB() {
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(securityProvider);
        Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        dynamoDBClient.setRegion(usWest2);
        return dynamoDBClient;
    }
}
