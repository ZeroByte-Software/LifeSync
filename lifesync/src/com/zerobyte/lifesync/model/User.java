package com.zerobyte.lifesync.model;

import java.util.Vector;

//Bean class used to hold data about a single restriction. 
public class User {
	int user_id;
	String password;
	String first_name;
	String last_name;
	boolean is_admin;
	String email;

	public User() {
		this.password = "";
		this.first_name = "";
		this.last_name = "";
		this.is_admin = false;
		this.email = "";
	}

	/**
	 * @param passwd
	 * @param fname
	 * @param lname
	 * @param isAdmin
	 * @param email
	 */
	public User(int userid, String username, String passwd, String fname,
			String lname, boolean isAdmin) {
		this.user_id = userid;
		this.password = passwd;
		this.first_name = fname;
		this.last_name = lname;
		this.is_admin = isAdmin;
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public String getFirst_name() {
		return first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public boolean getIs_admin() {
		return is_admin;
	}

	public String getEmail() {
		return email;
	}

	public int getUserid() {
		return user_id;
	}

	public void setPassword(String thePassword) {
		password = thePassword;
	}

	public void setFirst_name(String theFirstName) {
		first_name = theFirstName;
	}

	public void setLast_name(String theLastName) {
		last_name = theLastName;
	}

	public void setAdmin(boolean theIsAdmin) {
		is_admin = theIsAdmin;
	}

	public void setEmail(String theEmail) {
		email = theEmail;
	}

	public void setUserid(int id) {
		user_id = id;
	}

	public void addFriend(int friendID) {

	}

	public void removeFriend(int friendID) {

	}

};
