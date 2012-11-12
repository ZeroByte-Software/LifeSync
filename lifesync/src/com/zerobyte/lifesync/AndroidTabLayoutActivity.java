package com.zerobyte.lifesync;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class AndroidTabLayoutActivity extends TabActivity {

	public int currentTab = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_androidtablayout);

		TabHost tabHost = getTabHost();

		// Tab for Schedule
		Intent scheduleIntent = new Intent(this, ScheduleActivity.class);
		TabSpec schedulespec = tabHost.newTabSpec("Schedule");
		// setting Title and Icon for the Tab
		schedulespec.setIndicator("Schedule");
		schedulespec.setContent(scheduleIntent);

		// Tab for Contacts
		Intent contactIntent = new Intent(this, FriendListActivity.class);
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

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			public void onTabChanged(String tabId) {
				currentTab = getTabHost().getCurrentTab();
				invalidateOptionsMenu();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		switch (currentTab) {
		case 0:
			new MenuInflater(this).inflate(R.menu.menu_schedule, menu);
			break;
		case 1:
			new MenuInflater(this).inflate(R.menu.menu_friendlist, menu);
			break;
		case 2:
			break;
		}

		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.addEvent_option:
			Intent AddEventIntent = new Intent(
					AndroidTabLayoutActivity.this, EventInputActivity.class);
			startActivity(AddEventIntent);
			return true;
		case R.id.addFriend_option:
			// LOGIC TO PROMPT FOR EMAIL WITH DIALOG TO ADD FRIEND
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
