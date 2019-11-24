package com.github.freebox.api.model.data;

import kong.unirest.JsonNode;

public class WifiAccessPointStation {
	private String id;
	private String mac;
	private String bssid;
	private String hostname;
	private LANHost host;
	private String state;
	private int inactive;
	private int conn_duration;
	private int rx_bytes;
	private int tx_bytes;
	private int tx_rate;
	private int rx_rate;
	private int signal;
	private WifiStationFlags flags;
	private WifiStationStats last_rx;
	private WifiStationStats last_tx;
	
	public String getId() {
		return id;
	}
	public String getMac() {
		return mac;
	}
	public String getBssid() {
		return bssid;
	}
	public String getHostname() {
		return hostname;
	}
	public LANHost getHost() {
		return host;
	}
	public String getState() {
		return state;
	}
	public int getInactive() {
		return inactive;
	}
	public int getConnDuration() {
		return conn_duration;
	}
	public int getRx_bytes() {
		return rx_bytes;
	}
	public int getTx_bytes() {
		return tx_bytes;
	}
	public int getTx_rate() {
		return tx_rate;
	}
	public int getRx_rate() {
		return rx_rate;
	}
	public int getSignal() {
		return signal;
	}
	public WifiStationFlags getFlags() {
		return flags;
	}
	public WifiStationStats getLast_rx() {
		return last_rx;
	}
	public WifiStationStats getLast_tx() {
		return last_tx;
	}
}
