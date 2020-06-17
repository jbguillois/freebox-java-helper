package com.github.freebox.api.model.data;

public class AppPermissions {
	
	private boolean settings;
	private boolean contacts;
	private boolean calls;
	private boolean explorer;
	private boolean downloader;
	private boolean parental;
	private boolean pvr;
	private boolean camera;
	private boolean home;
	private boolean player;
	private boolean tv;
	
	public AppPermissions() {
		settings = false;
		contacts = false;
		calls = false;
		explorer = false;
		downloader = false;
		parental = false;
		pvr = false;
		camera = false;
		home = false;
		player = false;
		tv = false;
	}
	
	public boolean canManageBoxCamera() {
		return camera;
	}

	public boolean canManageBoxHomeFeatures() {
		return home;
	}

	public boolean canManageBoxPlayer() {
		return player;
	}

	public boolean canManageBoxTVFeatures() {
		return tv;
	}

	public boolean canManageBoxSettings() {
		return settings;
	}

	public boolean canManageBoxContacts() {
		return contacts;
	}
	
	public boolean canManageBoxCalls() {
		return calls;
	}

	public boolean canManageBoxFileSystem() {
		return explorer;
	}

	public boolean CanManageBoxDownloader() {
		return downloader;
	}
	
	public boolean canManageBoxParentalControls() {
		return parental;
	}

	public boolean canManageBoxVideoRecordings() {
		return pvr;
	}
}
