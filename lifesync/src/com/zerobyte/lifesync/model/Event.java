package com.zerobyte.lifesync.model;

public class Event {
	
	String event_id = "";
    String name = "";
    String start_time = ""; 
    String end_time = "";
    String location = "";
    String description = ""; 
    String schedule_id = "";
    
	public String getEvent_id()
	{
		return event_id;
    }

	public String getName()
	{
        return name;
	}

	public String getStart_time()
	{
		return start_time;
	}

	public String getEnd_time()
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

	public String getSchedule_id()
	{
		return schedule_id;
	}
	
	public void setEvent_id(String theEvent_id)
	{
		event_id = theEvent_id;
    }

	public void setName(String theName)
	{
        name = theName;
	}

	public void setStart_time(String theStart_time)
	{
		start_time = theStart_time;
	}

	public void setEnd_time(String theEnd_time)
	{
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

	public void setSchedule_id(String theSchedule_id)
	{
		schedule_id = theSchedule_id;
	}
}
