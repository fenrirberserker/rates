package com.trader.app.core.service.sns;

public interface SnsService {

    void publish(String message);

    void createSNSTopic(String topic);
}