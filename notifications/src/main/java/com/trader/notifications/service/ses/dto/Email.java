package com.trader.notifications.service.ses.dto;

import lombok.Data;

@Data
public class Email {

    String from;
    String to;
    String subject;
    String message;
}
