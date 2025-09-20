package com.rates.app.analytics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.rates.app.analytics.service.BtcService;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping(value="/btc")
public class BTCController {

	@Autowired
	BtcService btcServiceImpl;



	@GetMapping(value = "/info", produces = "application/json")
	@ResponseBody
	public String btc(@PathParam("currency") String currency, @PathParam("value") Integer value) {

		String response = btcServiceImpl.getBTC(currency,value);

		return response;
	}
	

}
