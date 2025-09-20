package com.rates.config.scheduler;

import com.rates.app.analytics.service.BtcServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;

@Service
public class SchedulerService /*implements CommandLineRunner*/{

	@Autowired
	BtcServiceImpl btcServiceImpl;

	
	private void scheduleService() {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		
		//for persistance use a Callable to return max and min and pass value to every new callService()
		int max =0;
		int min =0;
		
		
		Runnable scheduled1 = ()->callService(max, min);
		Future<?> scheduledresult = service.scheduleAtFixedRate(scheduled1, 5, 3, TimeUnit.SECONDS);
	}

	private void callService(int max, int min) {
		RestTemplate template = new RestTemplate();
		ResponseEntity<String> response = template.getForEntity("https://blockchain.info/tobtc?currency=USD&value=50000", String.class);
		String body = response.getBody();
		System.out.println("Response "+body);

		Integer current = Integer.valueOf(body.toString());


		BiPredicate<Integer, Integer> pmax = (x,y)->{return x>y;};
		BiPredicate<Integer, Integer> pmin = (x,y)->{return x<y;};


		if(pmax.test(current, max)) {

		}

		if(pmin.test(current, max)) {

		}

		System.out.println("Maximum "+max+" Minimum "+min);

	}

	
	public void run(String... args) throws Exception {
		scheduleService();
	}
	
	
	

}
