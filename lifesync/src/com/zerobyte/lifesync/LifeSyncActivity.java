package com.zerobyte.lifesync;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.zerobyte.lifesync.model.*;

public class LifeSyncActivity extends Activity {
	protected static User loggedInUser = new User();
	
    /*
     * Displays toast with a specified string
     */
    public void showToast( String text )
    {
    	Context context = getApplicationContext();
    	Toast toast = Toast.makeText( context, text, Toast.LENGTH_SHORT);
    	toast.show();
    }
}
