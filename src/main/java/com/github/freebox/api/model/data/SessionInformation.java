package com.github.freebox.api.model.data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
	public ZonedDateTime getStartTime() {
		Instant inst = Instant.ofEpochSecond(start_time);
		ZonedDateTime startTime = ZonedDateTime.ofInstant(inst, ZoneId.systemDefault());
		return startTime;
	}
	public String getClientIp() {
		return client_ip;
	}
}
