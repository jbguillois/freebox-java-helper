package com.github.freebox.api.model;

public class ApplicationDefinition {
	private String app_id;
	private String app_name;
	private String app_version;
	private String device_name;
	
	public ApplicationDefinition(String id, String name, String version, String device) {
		this.app_id = id;
		this.app_name = name;
		this.app_version = version;
		this.device_name = device;
	}
	
	public String getAppId() {
		return app_id;
	}
	public String getAppName() {
		return app_name;
	}
	public String getAppVersion() {
		return app_version;
	}
	public String getDeviceName() {
		return device_name;
	}
}
