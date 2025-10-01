package com.trader.app.config.aws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import java.net.URI;

@Configuration
@Slf4j
public class Config {

    @Value("${app.isCloud}")
    private boolean isCloud;

    @Value("${aws.dynamodb.endpoint}")
    private String dynamodbEndpoint;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        DynamoDbClientBuilder builder = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create());

        if (!isCloud) {
            builder.endpointOverride(URI.create(dynamodbEndpoint));
        }

        return builder.build();
    }

    @Bean
    public SnsClient snsClient() {
        if (!isCloud) {
            return SnsClient.builder()
                    .region(Region.US_EAST_1)
                    .credentialsProvider(ProfileCredentialsProvider.create())
                    .endpointOverride(URI.create("http://localhost:4566"))
                    .build();
        }

        return SnsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }
}
