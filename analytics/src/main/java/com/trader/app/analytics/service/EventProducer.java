package com.trader.app.analytics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {
    
    @Autowired
    private KafkaBridge kafkaBridge;
    
    public void sendTradingEvent(Object event) {
        kafkaBridge.forwardToWebSocket(event);
    }
}