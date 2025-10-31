package com.trader.app.core.service.sns;


import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class SnsServiceImpl implements SnsService {

/*
    @Autowired
    AmazonSNS amazonSNS;*/
    String message = "Message";
    String localTopic = "arn:aws:sns:us-east-1:000000000000:BTCValue";
    String remoteTopic = "arn:aws:sns:us-east-1:912614154447:BTCValue";





    @Override
    public void publish(String message) throws URISyntaxException {
        pubTopic();

    }

    @Override
    public void createSNSTopic(String topic) {

        // Create an Amazon SNS topic.
        /*final CreateTopicRequest createTopicRequest = new CreateTopicRequest(topic);
        final CreateTopicResult createTopicResponse = amazonSNS.createTopic(createTopicRequest)

        // Print the topic ARN.
        System.out.println("TopicArn:" + createTopicResponse.getTopicArn());

        // Print the request ID for the CreateTopicRequest action.
        System.out.println("CreateTopicRequest: " + amazonSNS.getCachedResponseMetadata(createTopicRequest));*/
    }

    public void pubTopic() throws URISyntaxException {

        String message = "Message";
        String localTopic = "arn:aws:sns:us-east-1:000000000000:BTCValue";
        String remoteTopic = "arn:aws:sns:us-east-1:912614154447:BTCValue";
        SnsClient snsClient = SnsClient.builder()
                .endpointOverride(new URI("http://localhost:4566"))
                .region(Region.US_EAST_1)
                .build();

        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .topicArn(localTopic)
                    .build();

            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());
            snsClient.close();
        } catch (Exception e) {
            // Handle exception
        }

    }



}
