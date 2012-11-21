package com.zerobyte.lifesync;

import java.util.HashMap;

import android.app.Application;

public class LifeSyncApplication extends Application {

	private HashMap<Integer, ScheduleEvent> schedule_data = new HashMap<Integer, ScheduleEvent>();

	public HashMap<Integer, ScheduleEvent> getSchedule() {
		return this.schedule_data;
	}

	public void saveSchedule(HashMap<Integer, ScheduleEvent> schedule_data) {
		this.schedule_data = new HashMap<Integer, ScheduleEvent>(schedule_data);
	}

}
