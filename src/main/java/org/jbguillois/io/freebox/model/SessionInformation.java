package org.jbguillois.io.freebox.model;

public class SessionInformation {
    private boolean is_web_app;
    private String device_name;
    private String app_name;
    private String id;
    private long start_time;
    private String client_ip;
    
	public boolean isWebApp() {
		return is_web_app;
	}
	public String getDeviceName() {
		return device_name;
	}
	public String getAppName() {
		return app_name;
	}
	public String getId() {
		return id;
	}
	public long getStartTime() {
		return start_time;
	}
	public String getClientIp() {
		return client_ip;
	}
}
