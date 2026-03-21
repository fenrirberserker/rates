package com.trader.notifications.service.sns;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Service
@Slf4j
public class SnsServiceImpl implements SnsService {

    private final SnsClient snsClient;
    private final String topicArn;

    public SnsServiceImpl(
            SnsClient snsClient,
            @Value("${app.isCloud}") boolean isCloud,
            @Value("${aws.sns.topic.local-arn}") String localArn,
            @Value("${aws.sns.topic.remote-arn}") String remoteArn) {
        this.snsClient = snsClient;
        this.topicArn = isCloud ? remoteArn : localArn;
    }

    @Override
    public void publish(String message) {
        try {
            PublishResponse result = snsClient.publish(PublishRequest.builder()
                    .message(message)
                    .topicArn(topicArn)
                    .build());
            log.info("SNS message sent — id: {}, status: {}",
                    result.messageId(), result.sdkHttpResponse().statusCode());
        } catch (Exception e) {
            log.error("Failed to publish SNS message: {}", e.getMessage(), e);
        }
    }

    @Override
    public void createSNSTopic(String topic) {
        log.warn("createSNSTopic('{}') called but not yet implemented", topic);
    }
}
