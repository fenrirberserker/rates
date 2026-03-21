package com.trader.notifications.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.SesClientBuilder;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.SnsClientBuilder;

import java.net.URI;

/**
 * AWS client beans — each bean checks app.isCloud:
 *   false → points to LocalStack at localhost:4566 (local dev)
 *   true  → points to real AWS (production)
 *
 * All clients use ProfileCredentialsProvider so credentials are never
 * hardcoded in source — they come from ~/.aws/credentials.
 */
@Configuration
@Slf4j
public class AwsConfig {

    @Value("${app.isCloud}")
    private boolean isCloud;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.localstack.endpoint}")
    private String localstackEndpoint;

    @Bean
    public SnsClient snsClient() {
        SnsClientBuilder builder = SnsClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(ProfileCredentialsProvider.create());

        if (!isCloud) {
            builder.endpointOverride(URI.create(localstackEndpoint));
        }

        log.info("SnsClient configured — target: {}", isCloud ? "AWS" : localstackEndpoint);
        return builder.build();
    }

    @Bean
    public SesClient sesClient() {
        SesClientBuilder builder = SesClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(ProfileCredentialsProvider.create());

        if (!isCloud) {
            builder.endpointOverride(URI.create(localstackEndpoint));
        }

        log.info("SesClient configured — target: {}", isCloud ? "AWS" : localstackEndpoint);
        return builder.build();
    }

    @Bean
    public DynamoDbClient dynamoDbClient() {
        DynamoDbClientBuilder builder = DynamoDbClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(ProfileCredentialsProvider.create());

        if (!isCloud) {
            builder.endpointOverride(URI.create(localstackEndpoint));
        }

        log.info("DynamoDbClient configured — target: {}", isCloud ? "AWS" : localstackEndpoint);
        return builder.build();
    }
}