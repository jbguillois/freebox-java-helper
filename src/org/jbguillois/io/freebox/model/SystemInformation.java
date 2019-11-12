package org.jbguillois.io.freebox.model;

public class SystemInformation {
    private String mac;
    private String box_flavor;
    private int temp_cpub;
    private String disk_status;
    private boolean box_authenticated;
    private String board_name;
    private int fan_rpm;
    private int temp_sw;
    private String uptime;
    private long uptime_val;
    private String user_main_storage;
    private int temp_cpum;
    private String serial;
    private String firmware_version;
    
	public String getMac() {
		return mac;
	}
	public String getBox_flavor() {
		return box_flavor;
	}
	public int getTemp_cpub() {
		return temp_cpub;
	}
	public String getDisk_status() {
		return disk_status;
	}
	public boolean isBox_authenticated() {
		return box_authenticated;
	}
	public String getBoard_name() {
		return board_name;
	}
	public int getFan_rpm() {
		return fan_rpm;
	}
	public int getTemp_sw() {
		return temp_sw;
	}
	public String getUptime() {
		return uptime;
	}
	public long getUptime_val() {
		return uptime_val;
	}
	public String getUser_main_storage() {
		return user_main_storage;
	}
	public int getTemp_cpum() {
		return temp_cpum;
	}
	public String getSerial() {
		return serial;
	}
	public String getFirmware_version() {
		return firmware_version;
	}
    
}
