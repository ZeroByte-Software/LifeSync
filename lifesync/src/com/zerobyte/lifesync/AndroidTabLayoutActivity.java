package com.zerobyte.lifesync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;

import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.bump.api.BumpAPIIntents;
import com.bump.api.IBumpAPI;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;
import com.zerobyte.lifesync.AndroidTabLayoutActivity.MyExpandableListAdapter.ViewHolder;

public class AndroidTabLayoutActivity extends LifeSyncActivityBase {

	private LifeSyncApplication lfapp;

	public int currentTab = 0;
	private TabHost tabHost;

	private static final int ADD_EVENT = 0;

	// SCHEDULE VARIABLES
	boolean init_flag = false;
	private ListView schedule_listView;
	private ScheduleListAdapter schedule_adapter;

	HashMap<Integer, ScheduleEvent> schedule_data;
	List<ArrayList<TimeSlot>> time_slots_data;

	// FRIEND LIST VARIABLES
	final private String SERVER_URL = "http://54.245.83.84:8080/FBWebServer/android";
	final private int MAX_TIMEOUT = 5000;
	final private int MAX_RETRIES = 1;
	final private int HTTP_OK = 200;
	private static final String GROUP = "GROUP";
	private static final String CHILD = "CHILD";

	protected static final ColorStateList AndroidGreen = null;

	protected static final ColorStateList AndroidRed = null;
	private ExpandableListView friendListView;
	private MyExpandableListAdapter mAdapter;

	private ArrayList<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
	private ArrayList<ArrayList<Map<String, String>>> childData = new ArrayList<ArrayList<Map<String, String>>>();
	private ArrayList<Boolean> group_check_states = null;

	// BUMP VARIABLES
	private IBumpAPI api;
	private boolean isBumpEnabled = false;
	private boolean isBumpInit = false;

	private String myBumpName = "LifeSync User";
	private String myBumpData = "This is the data";
	private String myBumpRcvdEmail = "";
	private int myBumpRcvdUserID;

	private final ServiceConnection connection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder binder) {

			Log.i("LifeSync_Bump", "onServiceConnected");
			api = IBumpAPI.Stub.asInterface(binder);
			new Thread(new Runnable() {
				public void run() {
					try {
						api.configure("2156db3846b54a2693f1ddfab9db3b8f",
								myBumpName);
					} catch (RemoteException e) {
						Log.w("LifeSync_Bump", e);
					}
				}
			}).start();

			Log.d("LifeSync_Bump", "Service connected");
		}

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
					myBumpRcvdEmail = new String(
							intent.getByteArrayExtra("data")).split(":")[0];
					myBumpRcvdUserID = Integer.parseInt(new String(intent
							.getByteArrayExtra("data")).split(":")[1]);
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
					// construct the data to send
					myBumpData = "";

					Log.i("LifeSync", "EMAIL: " + loggedInUser.getEmail());
					Log.i("LifeSync", "id: " + loggedInUser.getUserid());
					// TODO take real data from User class
					myBumpData += loggedInUser.getEmail(); // email
					myBumpData += ":"; // seperator
					myBumpData += loggedInUser.getUserid(); // user_id

					api.send(channelID, myBumpData.getBytes());
				} else if (action.equals(BumpAPIIntents.NOT_MATCHED)) {
					Log.i("LifeSync_Bump", "Not matched.");
				} else if (action.equals(BumpAPIIntents.CONNECTED)) {
					Log.i("LifeSync_Bump", "Connected to Bump...");
					// api.enableBumping(); // wait till app to enable
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

		final int AndroidGreen = getResources().getColor(
				android.R.color.holo_green_dark);
		final int AndroidRed = getResources().getColor(
				android.R.color.holo_red_dark);
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				currentTab = tabHost.getCurrentTab();
				invalidateOptionsMenu();

				if (currentTab != 2) {
					// not within bump tab
					if (isBumpEnabled) {
						try {
							api.disableBumping();
						} catch (RemoteException e) {
							Log.w("LifeSync_Bump", e);
						}

						// unbindService(connection);
						// unregisterReceiver(receiver);

						isBumpEnabled = false;
					}
				} else if (currentTab == 2) {
					if (!isBumpInit) {
						// moving into the bump tab, initialize bump api service
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

						isBumpInit = true;
					}
					// disable the api until user clicks the bump button
					// try {
					// api.disableBumping();
					// } catch (RemoteException e) {
					// Log.w("LifeSync_Bump", e);
					// }
					isBumpEnabled = false;
					((Button) findViewById(R.id.btnBump))
							.setText("Enable Bump");
					((TextView) findViewById(R.id.statusBump))
							.setText("Disabled");
					((TextView) findViewById(R.id.statusBump))
							.setTextColor(AndroidRed);
				}
			}
		});

		// Get handler to Application
		lfapp = (LifeSyncApplication) getApplication();

		// SCHEDULE LOGIC
		schedule_data = new HashMap<Integer, ScheduleEvent>(lfapp.getSchedule());
		time_slots_data = new ArrayList<ArrayList<TimeSlot>>();

		if (!init_flag) {
			for (int i = 0; i < 24; i++) {
				ArrayList<TimeSlot> time_slots_by_time = new ArrayList<TimeSlot>();
				for (int j = 0; j < 7; j++) {
					time_slots_by_time.add(new TimeSlot(0));
				}
				time_slots_data.add(time_slots_by_time);
			}

			ScheduleEvent se = new ScheduleEvent("FIRST", "1-4", "2-6",
					"HERE1", "FIRST EVENT", "SELF");
			schedule_data.put(se.getEvent_id(), se);
			se = new ScheduleEvent("SECOND", "1-4", "2-6", "THERE2",
					"SECOND EVENT", "ALSOSELF");
			schedule_data.put(se.getEvent_id(), se);

			lfapp.saveSchedule(schedule_data);

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
		groupData = new ArrayList<Map<String, String>>();

		for (int i = 0; i < 3; i++) {
			HashMap<String, String> curGroupMap = new HashMap<String, String>();
			groupData.add(curGroupMap);
			switch (i) {
			case 0: {
				curGroupMap.put(GROUP, "Friend Requests");
				break;
			}
			case 1: {
				curGroupMap.put(GROUP, "Friends");
				break;
			}
			case 2: {
				curGroupMap.put(GROUP, "Friends Awaiting");
				break;
			}
			}

			if (i == 0) {
				ArrayList<Map<String, String>> children = new ArrayList<Map<String, String>>();
				for (int j = 0; j < 5; j++) {
					HashMap<String, String> curChildMap = new HashMap<String, String>();
					children.add(curChildMap);
					curChildMap.put(CHILD, (j % 2 == 0) ? "Eddy " + j : "Eton "
							+ j);

				}
				childData.add(children);
			} else {
				ArrayList<Map<String, String>> children = new ArrayList<Map<String, String>>();

				// HashMap<String, String> curChildMap = new HashMap<String,
				// String>();
				// children.add(curChildMap);
				childData.add(children);

			}

		}

		if (group_check_states == null) {
			group_check_states = new ArrayList<Boolean>(childData.get(1).size());
			for (int i = 0; i < childData.get(1).size(); i++) {
				group_check_states.add(false);
			}
		}

		// Setup adapter
		mAdapter = new MyExpandableListAdapter(this, groupData, // groupData
																// describes the
																// first-level
																// entries
				android.R.layout.simple_expandable_list_item_1, // Layout for
																// the
																// first-level
																// entries new
				new String[] { GROUP, CHILD }, // Key in the groupData maps to
												// display
				new int[] { android.R.id.text1, android.R.id.text2 }, // Data
																		// under
																		// "colorName"
																		// key
																		// goes
																		// into
																		// this
																		// TextView
				childData, // childData describes second-level entries
				R.layout.row_layout, // Layout for second-level entries
				new String[] { GROUP, CHILD }, // Keys in childData maps to
												// display
				new int[] { android.R.id.text1, R.id.label } // Data under the
																// keys above go
																// into these
																// TextViews
		);

		friendListView = (ExpandableListView) findViewById(android.R.id.list);
		friendListView.setAdapter(mAdapter);
		friendListView.expandGroup(1);
		getFriendList(loggedInUser.getEmail());

		// BUMP LOGIC GOES HERE
		final Button bumpbtn = (Button) findViewById(R.id.btnBump);
		bumpbtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (!isBumpEnabled) {
					try {
						api.enableBumping();
					} catch (RemoteException e) {
						Log.w("LifeSync_Bump", e);
					}

					isBumpEnabled = true;
					bumpbtn.setText("Disable Bump");
					((TextView) findViewById(R.id.statusBump))
							.setText("Enabled");
					((TextView) findViewById(R.id.statusBump))
							.setTextColor(AndroidGreen);

				} else {
					// already enabled, disable
					try {
						api.disableBumping();
					} catch (RemoteException e) {
						Log.w("LifeSync_Bump", e);
					}

					isBumpEnabled = false;
					bumpbtn.setText("Enable Bump");
					((TextView) findViewById(R.id.statusBump))
							.setText("Disabled");
					((TextView) findViewById(R.id.statusBump))
							.setTextColor(AndroidRed);
				}

			}
		});

	}

	private void getFriendList(String email) {
		AndroidHttpClient httpClient = new AndroidHttpClient(SERVER_URL);
		ParameterMap params = httpClient.newParams();

		httpClient.setConnectionTimeout(MAX_TIMEOUT);
		httpClient.setReadTimeout(MAX_TIMEOUT);
		httpClient.setMaxRetries(MAX_RETRIES);

		params.add("email", email);
		// params.add( "password", password );

		// Contact server using POST via separate thread
		httpClient.get("/friendlist", params, new AsyncCallback() {
			@Override
			public void onComplete(HttpResponse httpResponse) {
				int status = httpResponse.getStatus();

				if (status == HTTP_OK) {

					JSONArray jobj;

					String output = httpResponse.getBodyAsString();
					try {
						jobj = new JSONArray(output);
						for (int i = 0; i < jobj.length(); i++) {
							// showToast( "Output:" + jobj.getString(i));
							output = jobj.getString(i);
							HashMap<String, String> curChildMap = new HashMap<String, String>();
							childData.get(1).add(curChildMap);
							curChildMap.put(CHILD, output);
							group_check_states.add(false);

						}
						friendListView.setAdapter(mAdapter);
						friendListView = (ExpandableListView) findViewById(android.R.id.list);
						friendListView.expandGroup(1);
						Toast.makeText(getBaseContext(),
								"Friends Added From DB!", Toast.LENGTH_LONG)
								.show();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// Intent loginIntent = new
					// Intent(AndroidTabLayoutActivity.this,
					// AndroidTabLayoutActivity.class);
					// startActivity(loginIntent);
				} else
					showToast("Cannot get friendlist. Please try again.");
			}

			@Override
			public void onError(Exception e) {
				showToast("Server error. Please try again.");
				e.printStackTrace();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

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
			showAddFriendPopUp();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showAddFriendPopUp() {

		final AlertDialog.Builder friendBuilder = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		input.setSingleLine();
		input.setText("");
		friendBuilder.setView(input);
		friendBuilder.setTitle("Friend Request");
		friendBuilder.setMessage("E-mail address of friend: ");
		friendBuilder.setPositiveButton("Send Request",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String friend = input.getText().toString().trim();
						String message = "Friend Request sent to:" + friend;
						HashMap<String, String> curChildMap = new HashMap<String, String>();
						childData.get(2).add(curChildMap);
						curChildMap.put(CHILD, friend);
						Toast.makeText(getApplicationContext(), message,
								Toast.LENGTH_SHORT).show();
						mAdapter.notifyDataSetChanged();
					}
				});

		friendBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Remember, create doesn't show the dialog
		AlertDialog friendDialog = friendBuilder.create();
		friendDialog.show();

	}

	public class MyExpandableListAdapter extends SimpleExpandableListAdapter {
		private Context mcontext;;
		private List<? extends Map<String, ?>> mGroupData;
		private int mExpandedGroupLayout;
		private String[] mGroupFrom;
		private int[] mGroupTo;

		private List<? extends List<? extends Map<String, String>>> mChildData;
		private int mChildLayout;
		private int mLastChildLayout;
		private String[] mChildFrom;
		private int[] mChildTo;
		private int mGroupPosition;
		private int mChildPosition;

		public MyExpandableListAdapter(Context context,
				List<? extends Map<String, ?>> groupData, int groupLayout,
				String[] groupFrom, int[] groupTo,
				List<? extends List<? extends Map<String, String>>> childData,
				int childLayout, String[] childFrom, int[] childTo) {
			super(context, groupData, groupLayout, groupFrom, groupTo,
					childData, childLayout, childFrom, childTo);
			mcontext = context;
			mGroupData = groupData;
			mExpandedGroupLayout = groupLayout;
			mGroupFrom = groupFrom;
			mGroupTo = groupTo;

			mChildData = childData;
			mChildLayout = childLayout;
			mChildFrom = childFrom;
			mChildTo = childTo;
		}

		class ViewHolder {
			protected TextView text;
			protected Button button;
			protected CheckBox checkbox;
		}

		public TextView getGenericView() {
			// Layout parameters for the ExpandableListView
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, 64);

			TextView textView = new TextView(AndroidTabLayoutActivity.this);
			textView.setLayoutParams(lp);
			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			textView.setPadding(36, 0, 0, 0);
			return textView;
		}

		// TODO Auto-generated constructor stub

		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			mGroupPosition = groupPosition;
			mChildPosition = childPosition;
			View view = null;

			switch (groupPosition) {

			case 0:
				LayoutInflater inflater = getLayoutInflater();
				view = inflater.inflate(R.layout.row_layout, null);

				final ViewHolder viewHolder = new ViewHolder();
				viewHolder.text = (TextView) view.findViewById(R.id.label);

				viewHolder.button = (Button) view
						.findViewById(R.id.confirm_btn);
				viewHolder.button.setFocusable(false);
				viewHolder.button.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						// childData.get(groupPosition).get(childPosition).put(IS_EVEN,
						// "Friend Confirmed");
						// childData.get(0).get(0).put(IS_EVEN, "New Friend");
						HashMap<String, String> curChildMap = new HashMap<String, String>();
						childData.get(1).add(curChildMap);
						// childData.get(groupPosition).get(childPosition).put(IS_EVEN,
						// "Friend Confirmed");
						curChildMap.put(
								CHILD,
								(String) mChildData.get(groupPosition)
										.get(childPosition).get(mChildFrom[1]));
						childData.get(0).remove(childPosition);
						// childData.get(1).get(0).put(IS_EVEN,
						// (String)childData.get(mGroupPosition).get(mChildPosition).get(mChildFrom[1]));
						// TextView tv=(TextView)findViewById(R.id.label);
						// tv.setText("Clicked");
						group_check_states.add(false);
						mAdapter.notifyDataSetChanged();
						Toast.makeText(getBaseContext(), "New Friend Added!",
								Toast.LENGTH_LONG).show();
					}
				});

				view.setTag(viewHolder);
				viewHolder.button
						.setTag(getChild(groupPosition, childPosition));
				ViewHolder holder = (ViewHolder) view.getTag();
				holder.text.setText((String) mChildData.get(groupPosition)
						.get(childPosition).get(mChildFrom[1]));
				break;

			case 1:

				LayoutInflater inflater1 = getLayoutInflater();
				view = inflater1.inflate(R.layout.row_layout_2, null);

				final ViewHolder viewHolder1 = new ViewHolder();
				viewHolder1.text = (TextView) view.findViewById(R.id.label2);
				viewHolder1.checkbox = (CheckBox) view
						.findViewById(R.id.checkMerge);

				// load the checkstates
				if ((group_check_states.get(childPosition)) == true) {
					viewHolder1.checkbox.setChecked(true);
				} else {
					viewHolder1.checkbox.setChecked(false);
				}

				viewHolder1.checkbox.setSaveEnabled(true);
				viewHolder1.checkbox
						.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								// TODO Auto-generated method stub
								// Model element = (Model)
								// viewHolder1.checkbox.getTag();
								// element.setSelected(buttonView.isChecked());
								// if(buttonView.isChecked())
								// viewHolder1.checkbox.setChecked(true);
								// else
								// viewHolder1.checkbox.setChecked(false);
								if (viewHolder1.checkbox.isChecked()) {
									group_check_states.set(childPosition, true);
								} else {
									group_check_states
											.set(childPosition, false);
								}

								// mAdapter.notifyDataSetChanged();
							}
						});

				view.setTag(viewHolder1);
				viewHolder1.checkbox.setTag(getChild(groupPosition,
						childPosition));
				ViewHolder holder1 = (ViewHolder) view.getTag();
				holder1.text.setText((String) mChildData.get(groupPosition)
						.get(childPosition).get(mChildFrom[1]));
				// holder1.checkbox.setChecked(((View)
				// mChildData.get(groupPosition).get(childPosition)).isSelected());
				break;

			case 2:
				LayoutInflater inflater2 = getLayoutInflater();
				view = inflater2.inflate(
						android.R.layout.simple_expandable_list_item_2, null);
				final ViewHolder viewHolder2 = new ViewHolder();
				viewHolder2.text = (TextView) view
						.findViewById(android.R.id.text2);
				view.setTag(viewHolder2);
				ViewHolder holder2 = (ViewHolder) view.getTag();
				holder2.text.setText((String) mChildData.get(groupPosition)
						.get(childPosition).get(mChildFrom[1]));
				break;
			}
			return view;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
				// update_time_slots_data();
			}

			// schedule_adapter.notifyDataSetChanged();
			lfapp.saveSchedule(schedule_data);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		schedule_data = new HashMap<Integer, ScheduleEvent>(lfapp.getSchedule());
		time_slots_data.clear();
		for (int i = 0; i < 24; i++) {
			ArrayList<TimeSlot> time_slots_by_time = new ArrayList<TimeSlot>();
			for (int j = 0; j < 7; j++) {
				time_slots_by_time.add(new TimeSlot(0));
			}
			time_slots_data.add(time_slots_by_time);
		}
		update_time_slots_data();
		schedule_adapter.notifyDataSetChanged();
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

				// If starttime is equal to endttime then fill all time slots
				if ((event_start_time[0] == event_end_time[0])
						&& (event_start_time[1] == event_end_time[1])) {
					for (int i = 0; i <= 6; i++) {
						for (int j = 0; j <= 23; j++) {
							time_slots_data.get(j).get(i).setStatus(1);
							time_slots_data.get(j).get(i).addEvent(se);
						}
					}
					continue;
				}

				// If starttime is greater than endttime then start from
				// monday-00:00 to endtime
				boolean sunday_midnight = false;
				if ((event_start_time[0] > event_end_time[0])
						|| ((event_start_time[0] == event_end_time[0]) && (event_start_time[0] > event_end_time[1]))) {
					for (int i = 0; i <= event_end_time[0]; i++) {
						int start = 0;
						int end = 23;

						if (i == event_end_time[0]) {
							end = event_end_time[1];
						}

						for (int j = start; j < end; j++) {
							time_slots_data.get(j).get(i).setStatus(1);
							time_slots_data.get(j).get(i).addEvent(se);
						}

						// SPECIAL CASE FOR 23:00-00:00
						if (i < event_end_time[0]) {
							time_slots_data.get(23).get(i).setStatus(1);
							time_slots_data.get(23).get(i).addEvent(se);
						}
					}

					event_end_time[0] = 6;
					event_end_time[1] = 23;
					sunday_midnight = true;
				}

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
					if (i < event_end_time[0]) {
						time_slots_data.get(23).get(i).setStatus(1);
						time_slots_data.get(23).get(i).addEvent(se);
					}
				}

				if (sunday_midnight) {
					time_slots_data.get(23).get(6).setStatus(1);
					time_slots_data.get(23).get(6).addEvent(se);
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		LoggingOutDialogFragment dialog = new LoggingOutDialogFragment();
		dialog.show(getFragmentManager(), "loggingOut");
	}

	// BUMP stuff
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void onDestroy() {
		if (isBumpInit) {
			Log.i("LifeSync_Bump", "onDestroy");
			unbindService(connection);
			unregisterReceiver(receiver);
			isBumpInit = false;
		}

		super.onDestroy();
	}

	public class LoggingOutDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.dialogLoggingOut)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									loggedInUser = null; // Remove reference to
															// currently logged
															// in user

									// Close activity and open up new
									// LoginActivity
									Intent backToLogin = new Intent(
											AndroidTabLayoutActivity.this,
											LoginActivity.class);
									startActivity(backToLogin);
									finish();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Do nothing...
								}
							});
			// Create the AlertDialog object and return it
			return builder.create();
		}
	}

}