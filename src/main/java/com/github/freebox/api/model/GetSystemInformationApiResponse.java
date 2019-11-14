package com.github.freebox.api.model;

import com.github.freebox.api.model.data.SystemInformation;

public class GetSystemInformationApiResponse extends ServerApiResponse {
	private SystemInformation result;
	
	public SystemInformation getResult() {
		return result;
	}
}
