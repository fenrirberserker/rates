package com.trader.app.analytics.controller;

import com.trader.app.analytics.service.EventProducer;
import com.trader.app.analytics.service.SseEventBridge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("${trading.api.base-path}")
@CrossOrigin(origins = "${trading.api.allowed-origins}")
public class TradingController {
    
    @Autowired
    private EventProducer eventProducer;
    
    @Autowired
    private SseEventBridge sseEventBridge;
    
    @PostMapping("${trading.api.event-endpoint}")
    public void sendEvent(@RequestBody Map<String, Object> event) {
        eventProducer.sendTradingEvent(event);
    }
    
    @GetMapping(value = "${trading.api.events-endpoint}", produces = "text/event-stream")
    public SseEmitter streamEvents() {
        SseEmitter emitter = new SseEmitter();
        sseEventBridge.addEmitter(emitter);
        return emitter;
    }
}