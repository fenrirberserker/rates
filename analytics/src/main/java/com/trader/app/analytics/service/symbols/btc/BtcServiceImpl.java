package com.trader.app.analytics.service.symbols.btc;

import com.trader.app.analytics.providers.blockchaininfo.BlockChainConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BtcServiceImpl implements BtcService {

	@Override
	public String getBTC(String currency, Integer value) {
		RestTemplate template = new RestTemplate();
		ResponseEntity<String> response =
				template.getForEntity(BlockChainConstants.URL+"?"
						+"currency="+currency+"&value="+value, String.class);
		String body = response.getBody();
		System.out.println("Response "+body);
		
		return body;
	}

}
