package com.zerobyte.lifesync.model;

public class Friend {
	String user_name = "";
    String f_username = "";
    String status = ""; 
    
	public String getUser_name()
	{
		return user_name;
    }

	public String getF_username()
	{
        return f_username;
	}

	public String getStatus()
	{
		return status;
	}
	
	public void setUser_name(String theUserName)
	{
		user_name = theUserName;
    }

	public void setF_username(String theF_username)
	{
		f_username = theF_username;
	}

	public void setStatus(String theStatus)
	{
		status = theStatus;
	}
}
