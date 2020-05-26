package com.github.freebox.api.model;

import com.github.freebox.api.model.data.ParentalFilterRule;

public class GetParentalFilterRuleResponse extends ServerApiResponse {
	private ParentalFilterRule result;
	
	public ParentalFilterRule getResult() {
		return result;
	}
}
