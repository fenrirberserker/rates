package com.trader.app.core.service.ses;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
public class MailServiceImpl implements MailService {

    @Value("${aws.ses.config.from}")
    private String from;

    @Value("${aws.ses.config.to}")
    private String to;

    private final SesClient sesClient;

    public MailServiceImpl() {
        this.sesClient = SesClient.builder()
                .region(Region.US_EAST_1)  // or your preferred region
                .build();
    }


    // Optional: Clean up resources when the application shuts down
    public void cleanup() {
        if (sesClient != null) {
            sesClient.close();
        }
    }

    @Override
    public void sendEmail() {
        try {
            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .destination(Destination.builder()
                            .toAddresses(to)
                            .build())
                    .message(Message.builder()
                            .subject(Content.builder()
                                    .charset("UTF-8")
                                    .data("subject")
                                    .build())
                            .body(Body.builder()
                                    .html(Content.builder()
                                            .charset("UTF-8")
                                            .data("htmlBody")
                                            .build())
                                    .build())
                            .build())
                    .source(from)
                    .build();

            sesClient.sendEmail(emailRequest);
        } catch (SesException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}


