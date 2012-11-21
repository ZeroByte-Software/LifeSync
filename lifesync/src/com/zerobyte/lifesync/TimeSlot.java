package com.zerobyte.lifesync;

import java.util.ArrayList;
//import java.util.HashMap;

public class TimeSlot {

	// 0 is empty, 1 is self, 2 is else
	private int status = 0;
	
//	private HashMap<String, String> schedule_id_list = new HashMap<String, String>();
	private ArrayList<Integer> schedule_id_list = new ArrayList<Integer>();
	
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
//		schedule_id_list.put(String.valueOf(se.getEvent_id()), "filled");
		schedule_id_list.add(se.getEvent_id());
	}
	
	public void removeEvent(ScheduleEvent se) {
		schedule_id_list.remove(Integer.valueOf(se.getEvent_id()));
	}
	
//	public HashMap<String, String> getEventIds () {
//		return schedule_id_list;
//	}
	
	public ArrayList<Integer> getEventIds () {
		return schedule_id_list;
	}
}
