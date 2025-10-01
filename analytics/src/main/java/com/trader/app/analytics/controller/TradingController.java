package com.trader.app.analytics.controller;

import com.trader.app.analytics.service.EventProducer;
import com.trader.app.analytics.service.KafkaBridge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("/api/trading")
@CrossOrigin(origins = "http://localhost:3000")
public class TradingController {
    
    @Autowired
    private EventProducer eventProducer;
    
    @Autowired
    private KafkaBridge kafkaBridge;
    
    @PostMapping("/event")
    public void sendEvent(@RequestBody Map<String, Object> event) {
        eventProducer.sendTradingEvent(event);
    }
    
    @GetMapping(value = "/events", produces = "text/event-stream")
    public SseEmitter streamEvents() {
        SseEmitter emitter = new SseEmitter();
        kafkaBridge.addEmitter(emitter);
        return emitter;
    }
}