package com.trader.app.analytics.service.ses.dto;

import lombok.Data;

@Data
public class Email {

    String from;
    String to;
    String subject;
    String message;
}
