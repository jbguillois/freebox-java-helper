package com.github.freebox.api.model.data;

public class L3Connectivity {
	private String addr;
	private boolean active;
	private String af;
	private boolean reachable;
	private int last_activity;
	private int last_time_reachable;
	
	public String getAddr() {
		return addr;
	}
	public boolean isActive() {
		return active;
	}
	public String getAF() {
		return af;
	}
	public boolean isReachable() {
		return reachable;
	}
	public int getLastActivity() {
		return last_activity;
	}
	public int getLastTimeReachable() {
		return last_time_reachable;
	}
	
}
