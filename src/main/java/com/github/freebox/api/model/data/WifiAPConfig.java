package com.github.freebox.api.model.data;

public class WifiAPConfig {
	private String band;
	private int channel_width;
	private int primary_channel;
	private int secondary_channel;
	private boolean dfs_enabled;
	private WifiAPHTConfig ht;
	
	public String getBand() {
		return band;
	}
	public int getChannelWidth() {
		return channel_width;
	}
	public int getPrimaryChannel() {
		return primary_channel;
	}
	public int getSecondaryChannel() {
		return secondary_channel;
	}
	public boolean isDfsEnabled() {
		return dfs_enabled;
	}
	public WifiAPHTConfig getHt() {
		return ht;
	}
	
	
}
