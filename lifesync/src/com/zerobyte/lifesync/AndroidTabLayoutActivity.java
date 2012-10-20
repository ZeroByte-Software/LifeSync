package com.zerobyte.lifesync;


import android.app.TabActivity; 

import android.os.Bundle; 
import android.widget.TabHost; 
import android.widget.TabHost.TabSpec; 
import android.content.Intent;  
@SuppressWarnings("deprecation")
public class AndroidTabLayoutActivity extends TabActivity { 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.main); 
  
        TabHost tabHost = getTabHost(); 
  
        // Tab for Schedule
        Intent scheduleIntent = new Intent(this, ScheduleActivity.class);
        TabSpec schedulespec = tabHost.newTabSpec("Schedule"); 
        // setting Title and Icon for the Tab 
        schedulespec.setIndicator("Schedule"); 
        schedulespec.setContent(scheduleIntent); 
  
        // Tab for Contacts 
        Intent contactIntent = new Intent(this, LifeSync.class);
        TabSpec contactspec = tabHost.newTabSpec("Contacts"); 
        contactspec.setIndicator("Contacts"); 
        contactspec.setContent(contactIntent); 
  
        // Tab for Bump 
        Intent bumpIntent = new Intent(this, BumpActivity.class); 
        TabSpec bumpspec = tabHost.newTabSpec("Bump"); 
        bumpspec.setIndicator("Bump"); 
        bumpspec.setContent(bumpIntent); 
  
        // Adding all TabSpec to TabHost 
        tabHost.addTab(schedulespec); // Adding schedule tab 
        tabHost.addTab(contactspec); // Adding contacts tab 
        tabHost.addTab(bumpspec); // Adding bump tab 
        tabHost.setCurrentTab(0);
    } 
} 


