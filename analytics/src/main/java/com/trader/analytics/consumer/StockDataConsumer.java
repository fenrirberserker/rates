package com.trader.analytics.consumer;

import com.trader.domain.model.StockData;
import com.trader.analytics.service.StockDataSink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer that ingests stock data from core's producer
 * and feeds it into the reactive sink for downstream analysis.
 */
@Component
@Slf4j
public class StockDataConsumer {

    private final StockDataSink sink;

    public StockDataConsumer(StockDataSink sink) {
        this.sink = sink;
    }

    @KafkaListener(
        topics = "${kafka.topic.stock-data:stock-data}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(StockData data) {
        log.debug("Received: {} high={} low={}", data.symbol(), data.high(), data.low());
        sink.push(data);
    }
}
