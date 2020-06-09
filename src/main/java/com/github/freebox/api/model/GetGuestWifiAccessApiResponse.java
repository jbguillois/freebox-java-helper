package com.github.freebox.api.model;

import java.util.List;

import com.github.freebox.api.model.data.GuestWifiAccess;

public class GetGuestWifiAccessApiResponse extends ServerApiResponse {
	private List<GuestWifiAccess> result;
	
	public List<GuestWifiAccess> getResult() {
		return result;
	}
}
