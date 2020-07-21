package com.github.freebox.api.model;

import org.apache.commons.lang3.StringUtils;

public class ServerApiVersionApiResponse {
	private String box_model_name;
	private String api_base_url;
	private String https_port;
	private String device_name;
	private String https_available;
	private String box_model;
	private String api_domain;
	private String uid;
	private String api_version;
	private String device_type;
	
	public String getBoxModelName() {
		return box_model_name;
	}
	public String getApiBaseUrl() {
		return api_base_url;
	}
	public String getHttpsPort() {
		return https_port;
	}
	public String getDeviceName() {
		return device_name;
	}
	public boolean isHttpsAvailable() {
		if("true".contentEquals(https_available))
			return true;
		return false;
	}
	public String getBoxModel() {
		return box_model;
	}
	public String getApiDomain() {
		return api_domain;
	}
	public String getUid() {
		return uid;
	}
	public String getApiVersion() {
		return api_version;
	}
	public int getApiVersionNumber() {
		if(StringUtils.isAlpha(api_version)) return 0;
		if(StringUtils.isNumeric(api_version.substring(0, 1))) return Integer.valueOf(api_version.substring(0, 1)).intValue();
		return 0;
	}
	public String getDeviceType() {
		return device_type;
	}
	
	public String getApiEndpoint() {
		StringBuffer buf = new StringBuffer();
		
		if(isHttpsAvailable())
			buf.append("https://");
		else
			buf.append("http://");
		buf.append(getApiDomain());
		buf.append(":");
		buf.append(getHttpsPort());
		buf.append(getApiBaseUrl());
		buf.append("v");
		buf.append(getApiVersion().substring(0,1));
		
		
		return buf.toString();
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append(getDeviceName());
		buf.append(" "+getUid());
		buf.append(" (");
		buf.append(getDeviceType());
		buf.append("), ");
		
		buf.append("API URL: ");
		buf.append(getApiEndpoint());
		
		return buf.toString();
	}
}
