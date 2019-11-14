package com.github.freebox.api.model;

import java.util.List;

import com.github.freebox.api.model.data.CallEntry;

public class GetCallEntriesApiResponse extends ServerApiResponse {
	private List<CallEntry> result;
	
	public List<CallEntry> getResult() {
		return result;
	}
}
