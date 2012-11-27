/**
 * 
 */
package com.zerobyte.lifesync.model;

import java.io.IOException;

/**
 * @author andrewyoon
 *
 */
public class Admin extends User {

	/**
	 * @throws IOException 
	 * 
	 */
	public Admin() throws IOException {
		// something wrong, an empty Admin class should not be created
		throw new IOException("Admin class cannot be empty");
	}

	/**
	 * @param passwd
	 * @param fname
	 * @param lname
	 * @param isAdmin
	 * @param email
	 */
	public Admin(int userid, String username, String passwd, String fname, String lname,
			boolean isAdmin, String email, int fbID) {
		super(userid, username, passwd, fname, lname, isAdmin, email);
		// TODO Auto-generated constructor stub
	}
	
	public void editUser(int userID) {
		/*
		 * TODO edit target user.. userID
		 * how to edit? take more arguments?
		 * 
		 */
		
	}

}
