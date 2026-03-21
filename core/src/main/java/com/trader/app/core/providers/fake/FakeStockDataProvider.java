package com.trader.app.core.providers.fake;

import com.trader.app.config.StockProperties;
import com.trader.domain.model.StockData;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class FakeStockDataProvider {

    private final StockProperties properties;
    private final Random random = new Random();

    public FakeStockDataProvider(StockProperties properties) {
        this.properties = properties;
    }

    public StockData getQuote(String symbol) {
        StockProperties.FakeDataConfig fake = properties.data().fake();
        double base = fake.basePriceMin() + random.nextDouble() * fake.basePriceRange();
        return new StockData(
                symbol,
                properties.data().typeOf(symbol),
                base + random.nextDouble() * fake.priceVariation(),   // high
                base - random.nextDouble() * fake.priceVariation(),   // low
                base + random.nextDouble() * fake.ohlcVariation(),    // open
                base + random.nextDouble() * fake.ohlcVariation(),    // close
                fake.volumeMin() + (long) (random.nextDouble() * fake.volumeRange())  // volume
        );
    }
}