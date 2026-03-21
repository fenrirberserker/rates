package com.trader.notifications.service.sns;

public interface SnsService {

    void publish(String message);

    void createSNSTopic(String topic);
}
