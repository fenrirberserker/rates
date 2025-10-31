package com.trader.app.core.providers.streams;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class InMemoryStockQueue {
    
    private final BlockingQueue<StockData> queue = new LinkedBlockingQueue<>();
    
    public void publish(StockData stockData) {
        try {
            queue.put(stockData);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public StockData consume() throws InterruptedException {
        return queue.take();
    }
    
    public int size() {
        return queue.size();
    }
}
