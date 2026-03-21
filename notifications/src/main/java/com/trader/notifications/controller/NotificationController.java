package com.trader.notifications.controller;

import com.trader.notifications.service.ses.MailService;
import com.trader.notifications.service.sns.SnsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final MailService mailService;
    private final SnsService snsService;

    public NotificationController(MailService mailService, SnsService snsService) {
        this.mailService = mailService;
        this.snsService = snsService;
    }

    @GetMapping(value = "/send-email", produces = "application/json")
    public void sendMail() {
        mailService.sendEmail();
    }

    @GetMapping("/create-topic")
    public void createTopic() {
        snsService.createSNSTopic("BTCValue");
    }

    @GetMapping("/publish")
    public void publish() {
        snsService.publish("SNS test");
    }
}
