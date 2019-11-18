package com.github.freebox.api.model.data;

public class LANInterface {
	private String name;
	private int host_count;
	
	public LANInterface(String name, int count) {
		this.name = name;
		this.host_count = count;
	}
	
	public String getName() {
		return name;
	}
	public int getHostCount() {
		return host_count;
	}
}
