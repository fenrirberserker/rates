package com.trader.app.core.service.symbols.btc;

import reactor.core.publisher.Mono;

public interface BtcService {

    Mono<String> getBTC(String currency, Integer value);
}