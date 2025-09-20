package com.trader.app.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Includes info about currency and quantity
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conditions {

    private String currency;
    private Integer quantity;
}
