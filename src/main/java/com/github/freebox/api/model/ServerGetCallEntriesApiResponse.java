package com.github.freebox.api.model;

import java.util.List;

public class ServerGetCallEntriesApiResponse extends ServerApiResponse {
	private List<CallEntry> result;
	
	public List<CallEntry> getResult() {
		return result;
	}
}