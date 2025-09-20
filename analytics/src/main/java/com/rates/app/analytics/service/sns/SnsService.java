package com.rates.app.analytics.service.sns;

import java.net.URISyntaxException;

public interface SnsService {

    public void publish(String message) throws URISyntaxException;
    public void createSNSTopic(String topic);
}
