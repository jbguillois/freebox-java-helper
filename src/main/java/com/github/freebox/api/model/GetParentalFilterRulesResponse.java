package com.github.freebox.api.model;

import java.util.List;

import com.github.freebox.api.model.data.ParentalFilterRule;

public class GetParentalFilterRulesResponse extends ServerApiResponse {
	private List<ParentalFilterRule> result;
	
	public List<ParentalFilterRule> getResult() {
		return result;
	}
}
