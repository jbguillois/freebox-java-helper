package com.github.freebox.api.model.data;

public class WifiStationStats {
	private int bitrate;
	private int mcs;
	private int vht_mcs;
	private String width;
	private boolean shortgi;
	
	public int getBitrate() {
		return bitrate;
	}
	public int getMcs() {
		return mcs;
	}
	public int getVhtMcs() {
		return vht_mcs;
	}
	public String getWidth() {
		return width;
	}
	public boolean isShortGIEnabled() {
		return shortgi;
	}
}
