package com.trader.app.analytics.controller;

import com.trader.app.analytics.service.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/trading")
@CrossOrigin(origins = "http://localhost:3000")
public class TradingController {
    
    @Autowired
    private EventProducer eventProducer;
    
    @PostMapping("/event")
    public void sendEvent(@RequestBody Map<String, Object> event) {
        eventProducer.sendTradingEvent(event);
    }
}