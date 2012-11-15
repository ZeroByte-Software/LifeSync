package com.zerobyte.lifesync.model;

import java.util.Calendar;
import java.util.Vector;

public class Schedule {
	 int schedule_id;
	 String user_name;
	 Vector<Event> schedule;
	 boolean isActive;
	 
	 public Schedule() {
		 schedule_id = -1;
		 user_name = "";
		 schedule = new Vector<Event>();
	 }
	 
	 public Schedule(int schID, String name) {
		 this.schedule_id = schID;
		 this.user_name = name;
		 this.schedule = new Vector<Event>();
	 }
	    
	 public String getUser_name()
	 {
		 return user_name;
	 }
	
	 public int getSchedule_id()
	 {
		 return schedule_id;
	 }
	
	 public void setUser_name(String theUserName)
	 {
		 user_name = theUserName;
	 }
	 
	 public void setSchedule_id(int theSchedule_id)
	 {
		 schedule_id = theSchedule_id;
	 }
	 
	 public void addEvent(Event e) {
		 
	 }
	 
	 public void addEvent(String name, Calendar sTime, Calendar eTime, String location,
			 String description) {
		 
	 }
	 
	 public void deleteEvent(Event e) {
		 
	 }
	 
	 public void editEvent(int eventID) {
		 
	 }
	 
	 public void setActive() {
		 this.isActive = true;
	 }
	 
	 public void setInactive() {
		 this.isActive = false;
	 }
	 
	 public void viewSchedule() {
		 
	 }
	 
	 public void callbump() {
		 
	 }
}
