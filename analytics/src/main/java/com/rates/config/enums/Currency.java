package com.rates.config.enums;

public enum Currency {

	USD("USD"),
	CAD("CAD"),
	EUR("EUR");
	
	
	public final String value;
	
	private Currency(String value) {
		this.value = value;
	}

}
