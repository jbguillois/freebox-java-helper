package com.github.freebox.api.model;

import com.github.freebox.api.model.data.WifiGlobalConfiguration;

public class GetWifiGlobalConfigurationApiResponse extends ServerApiResponse {
	private WifiGlobalConfiguration result;
	
	public WifiGlobalConfiguration getResult() {
		return result;
	}
}
