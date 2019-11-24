package com.github.freebox.api.model.data;

import kong.unirest.JsonNode;

public class WifiAccessPoint {
	private String id;
	private String name;
	private WifiAPStatus status;
	private JsonNode capabilities;
	private WifiAPConfig config;
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public WifiAPStatus getStatus() {
		return status;
	}
	public JsonNode getCapabilities() {
		return capabilities;
	}
	public WifiAPConfig getConfig() {
		return config;
	}
	
}
