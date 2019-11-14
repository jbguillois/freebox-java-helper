package com.github.freebox.api.model;

public class ServerAuthorizeApiRequest {
	private String app_id;
	private String app_name;
	private String app_version;
	private String device_name;
	
	public String getAppId() {
		return app_id;
	}
	public void setAppId(String app_id) {
		this.app_id = app_id;
	}
	public String getAppName() {
		return app_name;
	}
	public void setAppName(String app_name) {
		this.app_name = app_name;
	}
	public String getAppVersion() {
		return app_version;
	}
	public void setAppVersion(String app_version) {
		this.app_version = app_version;
	}
	public String getDeviceName() {
		return device_name;
	}
	public void setDeviceName(String device_name) {
		this.device_name = device_name;
	}
}
