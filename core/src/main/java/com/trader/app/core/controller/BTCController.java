package com.trader.app.core.controller;

import com.trader.app.core.service.symbols.btc.BtcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/btc")
public class BTCController {

	@Autowired
	BtcService btcServiceImpl;

	@GetMapping(value = "/info", produces = "application/json")
	public String btc(@RequestParam("currency") String currency, @RequestParam("value") Integer value) {
		return btcServiceImpl.getBTC(currency, value);
	}
}