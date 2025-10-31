package com.trader.app.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {
    
    @Autowired
    private SseEventBridge sseEventBridge;
    
    public void sendTradingEvent(Object event) {
        sseEventBridge.sendToClients(event);
    }
}
