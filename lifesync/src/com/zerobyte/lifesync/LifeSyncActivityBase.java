/* LifeSyncActivityBase.java
 * 
 * Base activity class for LifeSync
 * 
 */

package com.zerobyte.lifesync;

import com.zerobyte.lifesync.model.User;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

public class LifeSyncActivityBase extends Activity {
	
	LifeSyncApplication lfapp;
	User user;
	
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get handler to Application and user
		lfapp = (LifeSyncApplication) getApplication();
		user = new User(lfapp.getUser());
				
		// Setup toast
		toast = Toast.makeText(getApplicationContext(),
				"", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		
	}

	/*
	 * Displays toast with a specified string
	 */
	public void showToast(String text) {
		toast.setText(text);
		toast.show();
	}
}
