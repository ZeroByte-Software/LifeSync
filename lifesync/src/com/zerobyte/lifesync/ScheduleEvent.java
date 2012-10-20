package com.zerobyte.lifesync;

public class ScheduleEvent {

	public ScheduleEvent(boolean filled_time_slot) {
		super();
		this.filled_time_slot = filled_time_slot;
	}

	private boolean filled_time_slot = false;
	
	

	public boolean isFilled_time_slot() {
		return filled_time_slot;
	}

	public void setFilled_time_slot(boolean filled_time_slot) {
		this.filled_time_slot = filled_time_slot;
	}
	
}
