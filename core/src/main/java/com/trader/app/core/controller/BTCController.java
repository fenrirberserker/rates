package com.trader.app.core.controller;

import com.trader.app.core.providers.real.blockchaininfo.BlockchainInfoStockDataProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/crypto")
public class BTCController {

    private final BlockchainInfoStockDataProvider provider;

    public BTCController(BlockchainInfoStockDataProvider provider) {
        this.provider = provider;
    }

    @GetMapping(value = "/convert", produces = "application/json")
    public Mono<String> convert(
            @RequestParam("symbol") String symbol,
            @RequestParam("currency") String currency,
            @RequestParam("value") Integer value) {
        return provider.convert(symbol, currency, value);
    }
}
