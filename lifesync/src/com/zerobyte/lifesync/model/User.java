package com.zerobyte.lifesync.model;

import java.util.Vector;

//Bean class used to hold data about a single restriction. 
public class User
{
	int user_id;
    String user_name;
    String password;
    String first_name; 
    String last_name;
    boolean is_admin;
    String email; 
    int fb_id;
    Vector<Schedule> scheduleList;
    
    public User() {
    	this.user_name = "";
    	this.password = "";
    	this.first_name = "";
    	this.last_name = "";
    	this.is_admin = false;
    	this.email = "";
    	this.fb_id = -1;
    	this.scheduleList = new Vector<Schedule>();
    }
    
	/**
	 * @param username
	 * @param passwd
	 * @param fname
	 * @param lname
	 * @param isAdmin
	 * @param email
	 * @param fbID
	 */
    public User(int userid, String username, String passwd, String fname, String lname, boolean isAdmin,
    		String email, int fbID) {
    	this.user_id = userid;
    	this.user_name = username;
    	this.password = passwd;
    	this.first_name = fname;
    	this.last_name = lname;
    	this.is_admin = isAdmin;
    	this.email = email;
    	this.fb_id = fbID;
    	this.scheduleList = new Vector<Schedule>();  
    }
    
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

	public boolean getIs_admin()
	{
		return is_admin;
	}

	public String getEmail()
	{
		return email;
	}

	public int getFBid()
	{
		return fb_id;
	}
	
	public int getUserid() {
		return user_id;
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

	public void setAdmin(boolean theIsAdmin)
	{
		is_admin = theIsAdmin;
	}

	public void setEmail(String theEmail)
	{
		email = theEmail;
	}

	public void setFBid(int theFBID)
	{
		fb_id = theFBID;
	}
	
	public void setUserid(int id) {
		user_id = id;
	}
	
	
	public void createSchedule(int schID) {
		/*
		 * TODO arguments:
		 * 
		 */
		
		this.scheduleList.add(new Schedule()); // should add some event first?
		
	}
	public void deleteSchedule(int schID) {
		/*
		 * TODO call db directly? or wait till some sort of update()?
		 */
		
		for (int i=0; i < this.scheduleList.size(); i++) {
			if (this.scheduleList.get(i).getSchedule_id() == schID) {
				this.scheduleList.remove(i);
			}
		}
		
		
	}
	public Schedule viewSchedule(int schID) {
		/*
		 * for a single event? or the whole set of schedule?
		 * TODO 
		 * 
		 */
		
		for (int i=0; i < this.scheduleList.size(); i++) {
			if (this.scheduleList.get(i).getSchedule_id() == schID) {
				return this.scheduleList.get(i);
			}
		}
		
		return null;
	}
	public void addFriend(int friendID) {
		
	}
	public void removeFriend(int friendID) {
		
	}
	
//  not sure why these are on our class diagram
//
//	public void addContact() {
//		
//	}
//	public void deleteContact() {
//		
//	}
};
