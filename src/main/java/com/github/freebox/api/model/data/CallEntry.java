package com.github.freebox.api.model.data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
	public ZonedDateTime getDatetime() {
		Instant inst = Instant.ofEpochSecond(datetime);
		ZonedDateTime dateTimeZDT = ZonedDateTime.ofInstant(inst, ZoneId.systemDefault());
		return dateTimeZDT;
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
	public boolean isNew() {
		return _new;
	}
	public int getContactId() {
		return contact_id;
	}
}
