package com.github.freebox.api.model;

import java.util.List;

import com.github.freebox.api.model.data.SessionInformation;
import com.github.freebox.api.model.data.WifiAccessPoint;

public class GetWifiAccessPointsApiResponse extends ServerApiResponse {
	private List<WifiAccessPoint> result;
	
	public List<WifiAccessPoint> getResult() {
		return result;
	}
}
