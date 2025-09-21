package com.trader.app.analytics.providers.streams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class StreamingService {

    @Autowired
    private StockDataProducer producer;

    @Autowired
    private StockDataConsumer consumer;

    @PostConstruct
    public void startStreaming() {
        System.out.println("Starting in-memory streaming service...");
        
        try {
            // Start consumer first
            consumer.startConsuming();
            
            // Start producer
            producer.startProducing();
            
            System.out.println("Streaming service started successfully");
        } catch (Exception e) {
            System.err.println("Error starting streaming service: " + e.getMessage());
        }
    }

    @PreDestroy
    public void stopStreaming() {
        System.out.println("Stopping streaming service...");
        
        producer.stop();
        consumer.stop();
        
        System.out.println("Streaming service stopped");
    }

    public void restartStreaming() {
        stopStreaming();
        startStreaming();
    }
}