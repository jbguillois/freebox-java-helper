package com.github.freebox.api.model.data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class LANHost {
	private L2Identification l2ident;
	private boolean active;
	private String id;
	private int last_time_reachable;
	private boolean persistent;
	private List<Hostname> names;
	private String vendor_name;
	private List<L3Connectivity> l3connectivities;
	private boolean reachable;
	private int last_activity;
	private boolean primary_name_manual;
	private String primary_name;
	
	public L2Identification getL2Identitification() {
		return l2ident;
	}
	public boolean isActive() {
		return active;
	}
	public String getId() {
		return id;
	}
	public ZonedDateTime getLastTimeReachable() {
		Instant inst = Instant.ofEpochSecond(last_time_reachable);
		ZonedDateTime lastTimeReachable = ZonedDateTime.ofInstant(inst, ZoneId.systemDefault());
		return lastTimeReachable;
	}
	public boolean isPersistent() {
		return persistent;
	}
	public List<Hostname> getNames() {
		return names;
	}
	public String getVendorName() {
		return vendor_name;
	}
	public List<L3Connectivity> getL3Connectivities() {
		return l3connectivities;
	}
	public boolean isReachable() {
		return reachable;
	}
	public int getLastActivity() {
		return last_activity;
	}
	public boolean isPrimaryNameManual() {
		return primary_name_manual;
	}
	public String getPrimaryName() {
		return primary_name;
	}
}
