package com.zerobyte.lifesync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.bump.api.IBumpAPI;
import com.bump.api.BumpAPIIntents;

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

	// BUMP VARIABLES
	private IBumpAPI api;
	private boolean isBumpEnabled = false;

	private final ServiceConnection connection = new ServiceConnection() {
		@Override
	    public void onServiceConnected(ComponentName className, IBinder binder) {

	        Log.i("LifeSync_Bump", "onServiceConnected");
	        api = IBumpAPI.Stub.asInterface(binder);
	        new Thread() {
	            public void run() {
	                try {
	                    api.configure("2156db3846b54a2693f1ddfab9db3b8f", "Bump User");
	                } catch (RemoteException e) {
	                	Log.w("BumpTest", e);
	                }

	            }
	        }.start();

	        Log.d("LifeSync_Bump", "Service connected");
	    }

	    @Override
	    public void onServiceDisconnected(ComponentName className) {
	        Log.d("LifeSync_Bump", "Service disconnected");
	    }
	};

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			try {
				if (action.equals(BumpAPIIntents.DATA_RECEIVED)) {
					Log.i("LifeSync_Bump",
							"Received data from: "
									+ api.userIDForChannelID(intent
											.getLongExtra("channelID", 0)));
					Log.i("LifeSync_Bump",
							"Data: "
									+ new String(intent
											.getByteArrayExtra("data")));
				} else if (action.equals(BumpAPIIntents.MATCHED)) {
					long channelID = intent
							.getLongExtra("proposedChannelID", 0);
					Log.i("LifeSync_Bump",
							"Matched with: "
									+ api.userIDForChannelID(channelID));
					api.confirm(channelID, true);
					Log.i("LifeSync_Bump", "Confirm sent");
				} else if (action.equals(BumpAPIIntents.CHANNEL_CONFIRMED)) {
					long channelID = intent.getLongExtra("channelID", 0);
					Log.i("LifeSync_Bump",
							"Channel confirmed with "
									+ api.userIDForChannelID(channelID));
					api.send(channelID, "This will be our data in whatever format".getBytes());
				} else if (action.equals(BumpAPIIntents.NOT_MATCHED)) {
					Log.i("LifeSync_Bump", "Not matched.");
				} else if (action.equals(BumpAPIIntents.CONNECTED)) {
					Log.i("LifeSync_Bump", "Connected to Bump...");
					api.enableBumping();
				}
			} catch (RemoteException e) {
			}
		}
	};

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
				
				if (currentTab != 2) {
					// moving away from bump tab
					if (isBumpEnabled) {
						unbindService(connection);
						unregisterReceiver(receiver);
						
						isBumpEnabled = false;
					}
				}
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

			ScheduleEvent se = new ScheduleEvent("lol", "1-4", "2-6", "here",
					"test man", "Self");
			schedule_data.put(se.getEvent_id(), se);

			init_flag = true;
		}

		update_time_slots_data();
		schedule_adapter = new ScheduleListAdapter(this, time_slots_data,
				schedule_data);

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

		// BUMP LOGIC GOES HERE
		Button bumpbtn = (Button) findViewById(R.id.btnBump);
		bumpbtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
//				new BumpHandler().execute((Void)null);
				
				if (!isBumpEnabled) {
					bindService(new Intent(IBumpAPI.class.getName()),
							connection, Context.BIND_AUTO_CREATE);
					Log.i("LifeSync_Bump", "boot");
					IntentFilter filter = new IntentFilter();
					filter.addAction(BumpAPIIntents.CHANNEL_CONFIRMED);
					filter.addAction(BumpAPIIntents.DATA_RECEIVED);
					filter.addAction(BumpAPIIntents.NOT_MATCHED);
					filter.addAction(BumpAPIIntents.MATCHED);
					filter.addAction(BumpAPIIntents.CONNECTED);
					registerReceiver(receiver, filter);
					
					isBumpEnabled = true;
				}
				else {
					// already enabled, disable
					unbindService(connection);
					unregisterReceiver(receiver);
					
					isBumpEnabled = false;
				}
				
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
						event_data.get("event_description"), "Self");

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
					if (event_end_time[0] == (i + 1)) {
						time_slots_data.get(23).get(i).setStatus(1);
						time_slots_data.get(23).get(i).addEvent(se);
					}
				}
			}
		}
	}
	
	// BUMP stuff
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	public void onDestroy() {
		Log.i("LifeSync_Bump", "onDestroy");
		unbindService(connection);
		unregisterReceiver(receiver);
		super.onDestroy();
	}
}
