package com.trader.analytics.controller;

import com.trader.analytics.service.analysis.Technical;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/analyse")
public class AnalysisController {

    private final Technical technical;

    public AnalysisController(Technical technical) {
        this.technical = technical;
    }

    /** Sample-based technical analysis on static JSON data. */
    @GetMapping("/technical")
    public Mono<String> technical() {
        return technical.readValues();
    }

    /** Live analysis on the last N Kafka-received data points for the given symbol. */
    @GetMapping("/live/{symbol}")
    public Mono<String> live(@PathVariable String symbol) {
        return technical.analyzeLive(symbol);
    }
}
