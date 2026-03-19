package com.trader.app.core.providers.streams;

import com.trader.app.core.service.StockDataSink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer — bridges the stock-data topic to the reactive SSE sink.
 *
 * Spring Kafka runs this listener on its own thread pool (separate from
 * Netty's event loop), so there is no risk of blocking the reactive pipeline.
 * The emit() call on StockDataSink is non-blocking, making this callback safe.
 */
@Component
@Slf4j
public class StockKafkaConsumer {

    private final StockDataSink stockDataSink;

    public StockKafkaConsumer(StockDataSink stockDataSink) {
        this.stockDataSink = stockDataSink;
    }

    @KafkaListener(
            topics = "${kafka.topic.stock-data:stock-data}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(StockData stockData) {
        log.debug("Kafka consumed: {}", stockData);
        stockDataSink.emit(stockData);
    }
}