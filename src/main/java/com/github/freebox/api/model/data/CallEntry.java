package com.github.freebox.api.model.data;

public class CallEntry {
	
	private String id;
	private String type;
	private long datetime;
	private String number;
	private String name;
	private int duration;
	private boolean _new;
	private int contact_id;
	
	public String getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public long getDatetime() {
		return datetime;
	}
	public String getNumber() {
		return number;
	}
	public String getName() {
		return name;
	}
	public int getDuration() {
		return duration;
	}
	public boolean is_new() {
		return _new;
	}
	public int getContact_id() {
		return contact_id;
	}
}
