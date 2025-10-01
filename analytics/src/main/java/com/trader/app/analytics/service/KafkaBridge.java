package com.trader.app.analytics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class KafkaBridge {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Value("${trading.websocket.destination}")
    private String websocketDestination;
    
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    
    public void forwardToWebSocket(Object event) {
        messagingTemplate.convertAndSend(websocketDestination, event);
        
        // Also send to SSE clients
        emitters.removeIf(emitter -> {
            try {
                emitter.send(event);
                return false;
            } catch (Exception e) {
                return true;
            }
        });
    }
    
    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
    }
}