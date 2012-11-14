package com.zerobyte.lifesync;

public class ScheduleEvent {

	private String event_name;
	private String event_start_time;
	private String event_end_time;
	private String event_location;
	private String event_description;
	private String event_owner = "Self";
	
	private int event_id;
	private static int counter = 0;

	public ScheduleEvent(String event_name, String event_start_time,
			String event_end_time, String event_location,
			String event_description, String event_owner) {
		this.event_name = event_name;
		this.event_start_time = event_start_time;
		this.event_end_time = event_end_time;
		this.event_location = event_location;
		this.event_description = event_description;
		this.event_owner = event_owner;
		
		this.event_id = counter;
		counter++;
	}
	
//	public ScheduleEvent(ScheduleEvent se) {
//		this.event_name = se.getEvent_name();
//		this.event_start_time = se.getEvent_start_time();
//		this.event_end_time = se.getEvent_end_time();
//		this.event_location = se.getEvent_location();
//		this.event_description = se.getEvent_description();
//		
//		this.event_id = se.getEvent_id();
//	}

	public String getEvent_name() {
		return event_name;
	}

	public void setEvent_name(String event_name) {
		this.event_name = event_name;
	}

	public String getEvent_start_time() {
		return event_start_time;
	}

	public void setEvent_start_time(String event_start_time) {
		this.event_start_time = event_start_time;
	}

	public String getEvent_end_time() {
		return event_end_time;
	}

	public void setEvent_end_time(String event_end_time) {
		this.event_end_time = event_end_time;
	}

	public String getEvent_location() {
		return event_location;
	}

	public void setEvent_location(String event_location) {
		this.event_location = event_location;
	}

	public String getEvent_description() {
		return event_description;
	}

	public void setEvent_description(String event_description) {
		this.event_description = event_description;
	}
	
	public String getEvent_owner() {
		return event_owner;
	}

	public void setEvent_owner(String event_owner) {
		this.event_owner = event_owner;
	}

	public int getEvent_id() {
		return event_id;
	}
}
