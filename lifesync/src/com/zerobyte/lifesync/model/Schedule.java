package com.zerobyte.lifesync.model;

public class Schedule {
	 String schedule_id = "";
	 String user_name = "";
	    
	public String getUser_name()
	{
		return user_name;
    }

	public String getSchedule_id()
	{
        return schedule_id;
	}
	
	public void setUser_name(String theUserName)
	{
		user_name = theUserName;
    }

	public void setSchedule_id(String theSchedule_id)
	{
		schedule_id = theSchedule_id;
	}
}
