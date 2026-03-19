package com.trader.app.core.controller;

import com.trader.app.core.service.analysis.Technical;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/technical")
    public Mono<String> technical() {
        return technical.readValues();
    }
}