package com.zerobyte.lifesync;

import java.util.HashMap;

import android.app.Application;

import com.zerobyte.lifesync.model.User;

public class LifeSyncApplication extends Application {

	private HashMap<Integer, ScheduleEvent> schedule_data = new HashMap<Integer, ScheduleEvent>();
	private User user = new User();
	
	public HashMap<Integer, ScheduleEvent> getSchedule() {
		return this.schedule_data;
	}

	public void saveSchedule(HashMap<Integer, ScheduleEvent> schedule_data) {
		this.schedule_data = new HashMap<Integer, ScheduleEvent>(schedule_data);
	}
	
	public User getUser() {
		return this.user;
	}
	
	public void saveUser(User user) {
		this.user = new User(user);
	}

}
