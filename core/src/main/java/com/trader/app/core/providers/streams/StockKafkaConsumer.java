package com.trader.app.core.providers.streams;

import com.trader.app.core.service.StockDataSink;
import com.trader.domain.model.StockData;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kafka consumer — bridges the stock-data topic to the reactive SSE sink.
 *
 * Implements ConsumerSeekAware to seek to the end of each partition on startup,
 * preventing replay of old messages that would overflow the SSE sink buffer.
 *
 * Spring Kafka runs this listener on its own thread pool (separate from
 * Netty's event loop), so there is no risk of blocking the reactive pipeline.
 * The emit() call on StockDataSink is non-blocking, making this callback safe.
 */
@Component
@Slf4j
public class StockKafkaConsumer implements ConsumerSeekAware {

    private final StockDataSink stockDataSink;

    public StockKafkaConsumer(StockDataSink stockDataSink) {
        this.stockDataSink = stockDataSink;
    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        log.info("Seeking to end of {} partition(s) — skipping historical backlog", assignments.size());
        callback.seekToEnd(assignments.keySet());
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