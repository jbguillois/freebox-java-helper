package com.github.freebox.api.model;

import java.util.List;

import com.github.freebox.api.model.data.SessionInformation;

public class GetSessionsApiResponse extends ServerApiResponse {
	private List<SessionInformation> result;
	
	public List<SessionInformation> getResult() {
		return result;
	}
}
