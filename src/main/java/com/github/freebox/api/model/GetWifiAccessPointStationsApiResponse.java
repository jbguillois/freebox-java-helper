package com.github.freebox.api.model;

import java.util.List;

import com.github.freebox.api.model.data.WifiAccessPointStation;

public class GetWifiAccessPointStationsApiResponse extends ServerApiResponse {
	private List<WifiAccessPointStation> result;
	
	public List<WifiAccessPointStation> getResult() {
		return result;
	}
}
