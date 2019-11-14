package com.github.freebox.api.model;

import java.util.List;

import com.github.freebox.api.model.data.LANHost;

public class GetLANInterfaceHostsApiResponse extends ServerApiResponse {
	private List<LANHost> result;
	
	public List<LANHost> getResult() {
		return result;
	}
}
