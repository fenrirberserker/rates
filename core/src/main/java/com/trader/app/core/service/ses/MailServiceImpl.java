package com.trader.app.core.service.ses;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    private final SesClient sesClient;
    private final String from;
    private final String to;

    public MailServiceImpl(
            SesClient sesClient,
            @Value("${aws.ses.config.from}") String from,
            @Value("${aws.ses.config.to}") String to) {
        this.sesClient = sesClient;
        this.from = from;
        this.to = to;
    }

    @Override
    public void sendEmail() {
        try {
            sesClient.sendEmail(SendEmailRequest.builder()
                    .source(from)
                    .destination(Destination.builder().toAddresses(to).build())
                    .message(Message.builder()
                            .subject(Content.builder().charset("UTF-8").data("subject").build())
                            .body(Body.builder()
                                    .html(Content.builder().charset("UTF-8").data("htmlBody").build())
                                    .build())
                            .build())
                    .build());
            log.info("Email sent from {} to {}", from, to);
        } catch (SesException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}