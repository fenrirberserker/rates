package com.trader.app.core.controller;

import com.trader.app.core.providers.streams.StockData;
import com.trader.app.core.service.StockDataSink;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("${trading.api.base-path}")
public class TradingController {

    private final StockDataSink stockDataSink;
    private final String eventType;

    public TradingController(StockDataSink stockDataSink,
                             @Value("${trading.api.event-type}") String eventType) {
        this.stockDataSink = stockDataSink;
        this.eventType = eventType;
    }

    /**
     * Manually inject a StockData event into the stream.
     * Useful for testing the UI without Kafka running.
     */
    @PostMapping("${trading.api.event-endpoint}")
    public void sendEvent(@RequestBody StockData stockData) {
        stockDataSink.emit(stockData);
    }

    /**
     * SSE stream — the React UI connects here and receives StockData
     * events as server-sent events whenever Kafka delivers new data.
     *
     * Each event has:
     *   id    — unique UUID for client deduplication
     *   event — "stock-update" (UI filters on this)
     *   data  — StockData serialized as JSON
     */
    @GetMapping(value = "${trading.api.events-endpoint}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<StockData>> streamEvents() {
        return stockDataSink.stream()
                .map(data -> ServerSentEvent.<StockData>builder()
                        .id(UUID.randomUUID().toString())
                        .event(eventType)
                        .data(data)
                        .build());
    }
}