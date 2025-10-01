package com.trader.app.analytics.service;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseEventBridge {
    
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    
    public void sendToClients(Object event) {
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