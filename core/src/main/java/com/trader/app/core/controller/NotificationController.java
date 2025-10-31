package com.trader.app.core.controller;

import com.trader.app.core.service.ses.MailService;
import com.trader.app.core.service.sns.SnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@RestController
@RequestMapping(value="/notification")
public class NotificationController {

	@Autowired
	MailService mailService;

	@Autowired
	SnsService snsService;


	@GetMapping(value = "/send-email", produces = "application/json")
	public void sendMail() {

		mailService.sendEmail();

	}

	@GetMapping(value= "/create-topic")
	public void createTopic(){
		snsService.createSNSTopic("BTCValue");
	}

	@GetMapping(value = "/publish")
	public void publish() throws URISyntaxException {
		snsService.publish("SNS test");
	}
	

}
