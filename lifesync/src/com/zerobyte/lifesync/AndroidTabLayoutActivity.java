package com.zerobyte.lifesync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class AndroidTabLayoutActivity extends Activity {

	public int currentTab = 0;
	private TabHost tabHost;

	private static final int ADD_EVENT = 0;

	// SCHEDULE VARIABLES
	boolean init_flag = false;
	private ListView schedule_listView;
	private ScheduleListAdapter schedule_adapter;

	HashMap<Integer, ScheduleEvent> schedule_data = null;
	List<ArrayList<TimeSlot>> time_slots_data = null;

	// FRIEND LIST VARIABLES

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_androidtablayout);

		tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();

		// Tab for Schedule
		TabHost.TabSpec schedulespec = tabHost.newTabSpec("Schedule");
		schedulespec.setContent(R.id.activity_schedule);
		schedulespec.setIndicator("Schedule");

		// Tab for Contacts
		TabHost.TabSpec contactspec = tabHost.newTabSpec("Contacts");
		contactspec.setContent(R.id.activity_friend_list);
		contactspec.setIndicator("Contacts");

		// Tab for Bump
		TabHost.TabSpec bumpspec = tabHost.newTabSpec("Bump");
		bumpspec.setContent(R.id.activity_bump);
		bumpspec.setIndicator("Bump");

		// Adding all TabSpec to TabHost
		tabHost.addTab(schedulespec); // Adding schedule tab
		tabHost.addTab(contactspec); // Adding contacts tab
		tabHost.addTab(bumpspec); // Adding bump tab
		tabHost.setCurrentTab(0);

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				currentTab = tabHost.getCurrentTab();
				invalidateOptionsMenu();
			}
		});

		// SCHEDULE LOGIC
		schedule_data = new HashMap<Integer, ScheduleEvent>();
		time_slots_data = new ArrayList<ArrayList<TimeSlot>>();

		if (!init_flag) {
			for (int i = 0; i < 24; i++) {
				ArrayList<TimeSlot> time_slots_by_time = new ArrayList<TimeSlot>();
				for (int j = 0; j < 7; j++) {
					time_slots_by_time.add(new TimeSlot(0));
				}
				time_slots_data.add(time_slots_by_time);
			}
			
			ScheduleEvent se = new ScheduleEvent("lol", "1-4", "2-6", "here", "test man", "Self");
			schedule_data.put(se.getEvent_id(), se);
			
			init_flag = true;
		}

		update_time_slots_data();
		schedule_adapter = new ScheduleListAdapter(this, time_slots_data, schedule_data);

		schedule_listView = (ListView) findViewById(R.id.schedule_list);
		// listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		// {
		// public void onItemClick(AdapterView<?> parentView, View childView,
		// int position, long id) {
		//
		// Intent displayEventIntent = new Intent(ScheduleActivity.this,
		// EventDisplayActivity.class);
		// startActivity(displayEventIntent);
		// }
		// });
		schedule_listView.setAdapter(schedule_adapter);

		// FRIEND LIST LOGIC

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
			Intent AddEventIntent = new Intent(AndroidTabLayoutActivity.this,
					EventInputActivity.class);
			startActivityForResult(AddEventIntent, ADD_EVENT);
			return true;

		case R.id.addFriend_option:
			// LOGIC TO PROMPT FOR EMAIL WITH DIALOG TO ADD FRIEND
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			if (requestCode == ADD_EVENT) {
				HashMap<String, String> event_data = (HashMap<String, String>) data
						.getSerializableExtra("event_data");
				ScheduleEvent se = new ScheduleEvent(
						event_data.get("event_name"),
						event_data.get("event_start_time"),
						event_data.get("event_end_time"),
						event_data.get("event_location"),
						event_data.get("event_description"),
						"Self");
				
				schedule_data.put(se.getEvent_id(), se);
				update_time_slots_data();

			}

			schedule_adapter.notifyDataSetChanged();
		}

	}

	public void update_time_slots_data() {
		if (schedule_data.size() > 0) {
			
			for (ScheduleEvent se : schedule_data.values()) {
				// SUPPOSE FORMAT WAS: DAY-TIME
				String event_start_time_str[] = se.getEvent_start_time().split(
						"-");
				String event_end_time_str[] = se.getEvent_end_time().split("-");
				int[] event_start_time = new int[2];
				int[] event_end_time = new int[2];

				event_start_time[0] = Integer.parseInt(event_start_time_str[0]);
				event_start_time[1] = Integer.parseInt(event_start_time_str[1]);
				event_end_time[0] = Integer.parseInt(event_end_time_str[0]);
				event_end_time[1] = Integer.parseInt(event_end_time_str[1]);

				for (int i = event_start_time[0]; i <= event_end_time[0]; i++) {
					int start = 0;
					int end = 23;

					if (i == event_start_time[0]) {
						start = event_start_time[1];
					}
					if (i == event_end_time[0]) {
						end = event_end_time[1];
					}

					for (int j = start; j < end; j++) {
						time_slots_data.get(j).get(i).setStatus(1);
						time_slots_data.get(j).get(i).addEvent(se);
					}
					
					// SPECIAL CASE FOR 23:00-00:00
					if(event_end_time[0] == (i + 1)) {
						time_slots_data.get(23).get(i).setStatus(1);
						time_slots_data.get(23).get(i).addEvent(se);
					}
				}
			}
		}
	}
}
