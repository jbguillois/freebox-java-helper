package com.github.freebox.api.model;

import com.github.freebox.api.model.data.ParentalFilterConfiguration;

public class GetParentalFilterConfigurationResponse extends ServerApiResponse {
	private ParentalFilterConfiguration result;
	
	public ParentalFilterConfiguration getResult() {
		return result;
	}
}
