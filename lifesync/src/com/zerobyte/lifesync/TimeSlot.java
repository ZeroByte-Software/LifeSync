package com.zerobyte.lifesync;

import java.util.ArrayList;
import java.util.HashMap;

public class TimeSlot {

	// 0 is empty, 1 is self, 2 is else
	private int status = 0;
	
	private HashMap<String, String> schedule_id_list = new HashMap<String, String>();

	public TimeSlot(int status) {
		super();
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public void addEvent(ScheduleEvent se) {
		schedule_id_list.put(String.valueOf(se.getEvent_id()), "filled");
	}
	
	public void removeEvent(ScheduleEvent se) {
		schedule_id_list.remove(String.valueOf(se.getEvent_id()));
	}
}
