package com.github.freebox.api.model.data;

public class GuestWifiAccess {
	private String id;
	private long remaining;
	private GuestWifiAccessParams params;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getRemaining() {
		return remaining;
	}
	public void setRemaining(long remaining) {
		this.remaining = remaining;
	}
	public GuestWifiAccessParams getParams() {
		return params;
	}
	public void setParams(GuestWifiAccessParams params) {
		this.params = params;
	}
	
}
