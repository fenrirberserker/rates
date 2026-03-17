package com.trader.app.core.providers.streams;

import com.trader.app.core.service.SseEventBridge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer that reads StockData from the stock-data topic
 * and forwards each message to all connected SSE clients.
 *
 * Spring manages the lifecycle of this listener automatically —
 * no manual start/stop needed.
 */
@Component
public class StockKafkaConsumer {

    @Autowired
    private SseEventBridge sseEventBridge;

    @KafkaListener(
            topics = "${kafka.topic.stock-data:stock-data}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(StockData stockData) {
        System.out.println("Kafka consumed: " + stockData);
        sseEventBridge.sendToClients(stockData);
    }
}