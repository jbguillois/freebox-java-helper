package com.github.freebox.api.model.data;

public class WifiGlobalConfiguration {
    private boolean enabled;
    private String mac_filter_state;

    public boolean isEnabled() {
		return enabled;
	}
	public String getMacFilterState() {
		return mac_filter_state;
	}
}
