package com.trader.app.core.controller;

import com.trader.app.core.service.symbols.btc.BtcService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/btc")
public class BTCController {

    private final BtcService btcService;

    public BTCController(BtcService btcService) {
        this.btcService = btcService;
    }

    @GetMapping(value = "/info", produces = "application/json")
    public Mono<String> btc(
            @RequestParam("currency") String currency,
            @RequestParam("value") Integer value) {
        return btcService.getBTC(currency, value);
    }
}