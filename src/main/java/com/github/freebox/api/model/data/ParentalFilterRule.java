package com.github.freebox.api.model.data;

public class ParentalFilterRule {
	
	private String id;
	private String[] macs;
	private String[] hosts;
	private String ip;
	private String desc;
	private boolean forced;
	private String forced_mode;
	private int tmp_mode_expire;
	private String tmp_mode;
	private String scheduling_mode;
	private String filter_state;
	private int current_mapping_idx;
	private int next_change;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String[] getMacs() {
		return macs;
	}
	public void setMacs(String[] macs) {
		this.macs = macs;
	}
	public String[] getHosts() {
		return hosts;
	}
	public void setHosts(String[] hosts) {
		this.hosts = hosts;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public boolean isForced() {
		return forced;
	}
	public void setForced(boolean forced) {
		this.forced = forced;
	}
	public String getForcedMode() {
		return forced_mode;
	}
	public void setForcedMode(String forced_mode) {
		this.forced_mode = forced_mode;
	}
	public int getTmpModeExpire() {
		return tmp_mode_expire;
	}
	public void setTmpModeExpire(int tmp_mode_expire) {
		this.tmp_mode_expire = tmp_mode_expire;
	}
	public String getTmpMode() {
		return tmp_mode;
	}
	public void setTmpMode(String tmp_mode) {
		this.tmp_mode = tmp_mode;
	}
	public String getSchedulingMode() {
		return scheduling_mode;
	}
	public void setSchedulingMode(String scheduling_mode) {
		this.scheduling_mode = scheduling_mode;
	}
	public String getFilterState() {
		return filter_state;
	}
	public void setFilterState(String filter_state) {
		this.filter_state = filter_state;
	}
	public int getCurrentMappingIndex() {
		return current_mapping_idx;
	}
	public void setCurrentMappingIndex(int current_mapping_idx) {
		this.current_mapping_idx = current_mapping_idx;
	}
	public int getNextChange() {
		return next_change;
	}
	public void setNextChange(int next_change) {
		this.next_change = next_change;
	}
}
