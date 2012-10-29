package com.zerobyte.lifesync.model;

//Bean class used to hold data about a single restriction. 
public class User
{
    String user_name = "";
    String password = "";
    String first_name = ""; 
    String last_name = "";
    String is_admin = "";
    String email = ""; 
    String fb_id = "";
    
	public String getUser_name()
	{
		return user_name;
    }

	public String getPassword()
	{
        return password;
	}

	public String getFirst_name()
	{
		return first_name;
	}

	public String getLast_name()
	{
		return last_name;
    }

	public String getIs_admin()
	{
		return is_admin;
	}

	public String getEmail()
	{
		return email;
	}

	public String getFb_id()
	{
		return fb_id;
	}
	
	public void setUser_name(String theUserName)
	{
		user_name = theUserName;
    }

	public void setPassword(String thePassword)
	{
        password = thePassword;
	}

	public void setFirst_name(String theFirstName)
	{
		first_name = theFirstName;
	}

	public void setLast_name(String theLastName)
	{
		last_name = theLastName;
    }

	public void setIs_admin(String theIsAdmin)
	{
		is_admin = theIsAdmin;
	}

	public void setEmail(String theEmail)
	{
		email = theEmail;
	}

	public void setFb_id(String theFBID)
	{
		fb_id = theFBID;
	}
};
