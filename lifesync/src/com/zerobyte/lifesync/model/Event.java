package com.zerobyte.lifesync.model;

import java.util.Calendar;

public class Event {
	
	int event_id;
    String name;
    Calendar start_time; 
    Calendar end_time;
    String location;
    String description; 
    int schedule_id; // for the schedule which owns this event??
    
    public Event() {
    	this.event_id = -1;
    	this.schedule_id = -1;
    }
    
    public Event(int eventID, String name, Calendar sTime, Calendar eTime, String location,
    		String desc, int schID) {
    	this.event_id = eventID;
    	this.name = name;
    	this.start_time = sTime; // TODO create new Calendar object and assign values..?
    	this.end_time = eTime;
    	this.location = location;
    	this.description = desc;
    	this.schedule_id = schID;
    }
    
	public int getEvent_id()
	{
		return event_id;
    }

	public String getName()
	{
        return name;
	}

	public Calendar getStart_time()
	{
		return start_time;
	}

	public Calendar getEnd_time()
	{
		return end_time;
    }

	public String getLocation()
	{
		return location;
	}

	public String getDescription()
	{
		return description;
	}

	public int getSchedule_id()
	{
		return schedule_id;
	}
	
	public void setEvent_id(int theEvent_id)
	{
		event_id = theEvent_id;
    }

	public void setName(String theName)
	{
        name = theName;
	}

	public void setStart_time(Calendar theStart_time)
	{
		// TODO want to take the argument as separate date and time, not Calendar object
		start_time = theStart_time;
	}

	public void setEnd_time(Calendar theEnd_time)
	{
		// TODO want to take the argument as separate date and time, not Calendar object
		end_time = theEnd_time;
    }

	public void setLocation(String theLocation)
	{
		location = theLocation;
	}

	public void setDescription(String theDescription)
	{
		description = theDescription;
	}

	public void setSchedule_id(int theSchedule_id)
	{
		schedule_id = theSchedule_id;
	}
}
