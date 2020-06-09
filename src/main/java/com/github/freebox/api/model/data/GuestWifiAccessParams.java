package com.github.freebox.api.model.data;

public class GuestWifiAccessParams {
	
	private int max_use_count;
	private String description;
	private String origin;
	private long duration;
	private String access_type;
	private String key;
	
	public int getMaxUseCount() {
		return max_use_count;
	}
	public void setMaxUseCount(int max_use_count) {
		this.max_use_count = max_use_count;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getAccessType() {
		return access_type;
	}
	public void setAccessType(String access_type) {
		this.access_type = access_type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	
}
