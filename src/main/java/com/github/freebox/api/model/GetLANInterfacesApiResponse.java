package com.github.freebox.api.model;

import java.util.List;

import com.github.freebox.api.model.data.LANInterface;

public class GetLANInterfacesApiResponse extends ServerApiResponse {
	private List<LANInterface> result;
	
	public List<LANInterface> getResult() {
		return result;
	}
}
