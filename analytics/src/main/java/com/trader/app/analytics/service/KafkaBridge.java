package com.trader.app.analytics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaBridge {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Value("${trading.websocket.destination}")
    private String websocketDestination;
    
    public void forwardToWebSocket(Object event) {
        messagingTemplate.convertAndSend(websocketDestination, event);
    }
}