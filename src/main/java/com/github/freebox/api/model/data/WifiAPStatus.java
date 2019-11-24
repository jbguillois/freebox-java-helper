package com.github.freebox.api.model.data;

public class WifiAPStatus {
	private String state;
	private int channel_width;
	private int primary_channel;
	private int secondary_channel;
	private int dfs_cac_remaining_time;
	
	public String getState() {
		return state;
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
	public int getDfsCacRemainingTime() {
		return dfs_cac_remaining_time;
	}
	
}
