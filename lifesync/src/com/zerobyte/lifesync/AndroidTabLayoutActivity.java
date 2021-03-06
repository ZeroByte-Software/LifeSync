package com.zerobyte.lifesync;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bump.api.BumpAPIIntents;
import com.bump.api.IBumpAPI;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.zerobyte.lifesync.model.User;

public class AndroidTabLayoutActivity extends LifeSyncActivityBase {

	
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
	private static final String GROUP = "GROUP";
	private static final String CHILD = "CHILD";

	protected static final ColorStateList AndroidGreen = null;

	protected static final ColorStateList AndroidRed = null;
	private ExpandableListView friendListView;
	private MyExpandableListAdapter mAdapter;

	private ArrayList<Map<String, String>> groupData;
	private ArrayList<ArrayList<Map<String, String>>> childData = new ArrayList<ArrayList<Map<String,String>>>();
	private ArrayList<User> friendlist;
	private ArrayList<User> ARfriendlist;
	private ArrayList<User> pendfriendlist;
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
					myBumpRcvdEmail = new String(
							intent.getByteArrayExtra("data")).split(":")[0];
					myBumpRcvdUserID = Integer.parseInt(new String(intent
							.getByteArrayExtra("data")).split(":")[1]);
					
					// TODO add friend using myBumpRcvdEmail
					addFriendviaBump(myBumpRcvdEmail);
					showToast(myBumpRcvdEmail);
					
					//mAdapter.notifyDataSetChanged();
					//showToast("New Friend Added!");
					
					
					
					
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

					Log.i("LifeSync", "EMAIL: " + user.getEmail());
					Log.i("LifeSync", "id: " + user.getUserid());
					// TODO take real data from User class
					myBumpData += user.getEmail(); // email
					myBumpData += ":"; // seperator
					myBumpData += user.getUserid(); // user_id

					api.send(channelID, myBumpData.getBytes());
				} else if (action.equals(BumpAPIIntents.NOT_MATCHED)) {
					Log.i("LifeSync_Bump", "Not matched.");
				} else if (action.equals(BumpAPIIntents.CONNECTED)) {
					Log.i("LifeSync_Bump", "Connected to Bump...");
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

				
				if (currentTab == 1) {
					// Expand all friend groups
					friendListView.expandGroup(0);
					friendListView.expandGroup(1);
					friendListView.expandGroup(2);
				} 
				
				if (currentTab != 2) {
					// not within bump tab, disable bump
					if (isBumpEnabled) {
						try {
							api.disableBumping();
						} catch (RemoteException e) {
							Log.w("LifeSync_Bump", e);
						}

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
			
			getScheduleEventList(user);
			lfapp.saveSchedule(schedule_data);

			init_flag = true;
		}

		update_time_slots_data();
		schedule_adapter = new ScheduleListAdapter(this, time_slots_data, lfapp);

		schedule_listView = (ListView) findViewById(R.id.schedule_list);
		schedule_listView.setAdapter(schedule_adapter);

		// FRIEND LIST LOGIC
		friendlist = new ArrayList<User>(lfapp.getFriendlist());
		
		groupData = new ArrayList<Map<String, String>>();

		for (int i = 0; i < 3; i++) {
			HashMap<String, String> curGroupMap = new HashMap<String, String>();
			groupData.add(curGroupMap);
			switch (i) {
			case 0: 
				curGroupMap.put(GROUP, "Friend Requests");
				break;
			
			case 1:
				curGroupMap.put(GROUP, "Friends");
				break;
		
			case 2: 
				curGroupMap.put(GROUP, "Friends Awaiting");
				break;
			
			}

			if (i == 0) {
				ArrayList<Map<String, String>> children = new ArrayList<Map<String, String>>();
				
//				for (int j = 0; j < 5; j++) {
//					HashMap<String, String> curChildMap = new HashMap<String, String>();
//					children.add(curChildMap);
//					curChildMap.put(CHILD, (j % 2 == 0) ? "Eddy " + j : "Eton "
//							+ j);
//
//				}
				
				childData.add(children);
			} else {
				ArrayList<Map<String, String>> children = new ArrayList<Map<String, String>>();
				childData.add(children);
			}

		}

		if (group_check_states == null) {
			group_check_states = new ArrayList<Boolean>(childData.get(1).size());
			for (int i = 0; i < childData.get(1).size(); i++) {
				group_check_states.add(false);
			}
		}
		
		getPendFriendList(user.getEmail());
		getFriendList(user.getEmail());
		getARFriendList(user.getEmail());

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
					// already enabled, so disable
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
	private void getPendFriendList(String email) {
		final LifeSyncHttpClient httpClient = new LifeSyncHttpClient();
		ParameterMap params = httpClient.newParams();

		params.add("email", email);

		// Contact server using POST via separate thread
		httpClient.get("/friendlist", params, new AsyncCallback() {
			@Override
			public void onComplete(HttpResponse httpResponse) {
				int status = httpResponse.getStatus();

				if (status == httpClient.HTTP_OK) {

					pendfriendlist = new ArrayList<User>();

					String output = httpResponse.getBodyAsString();
					try {
						JSONArray friend = new JSONArray(output);
						for (int i = 0; i < friend.getJSONArray(2).length(); i++) {
							int user_id;
							String first_name;
							String last_name;
					
							String email;
							user_id = friend.getJSONArray(2).getJSONObject(i).getInt("user_id");
							first_name = friend.getJSONArray(2).getJSONObject(i).getString("first_name");
							last_name = friend.getJSONArray(2).getJSONObject(i).getString("last_name");
							email = friend.getJSONArray(2).getJSONObject(i).getString("email");
							User userFriend = new User();
							userFriend.setUserid(user_id);
							userFriend.setFirst_name(first_name);
							userFriend.setLast_name(last_name);
							userFriend.setEmail(email);
							String friendname = first_name + " " + last_name;
							
							HashMap<String, String> curChildMap = new HashMap<String, String>();
							childData.get(0).add(curChildMap);
							curChildMap.put(CHILD, friendname);
							pendfriendlist.add(userFriend);
						}
						
						showToast("Pending Friends Added From DB!");
						//showToast( "Output:" + output.toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					showToast("Cannot get pendingfriendlist. Please try again.");
				}
			}

			@Override
			public void onError(Exception e) {
				showToast("Server error. Please try again.");
				e.printStackTrace();
			}
		});
	}
	
	private void getFriendList(String email) {
		final LifeSyncHttpClient httpClient = new LifeSyncHttpClient();
		ParameterMap params = httpClient.newParams();

		params.add("email", email);
		// params.add( "password", password );

		// Contact server using POST via separate thread
		httpClient.get("/friendlist", params, new AsyncCallback() {
			@Override
			public void onComplete(HttpResponse httpResponse) {
				int status = httpResponse.getStatus();

				if (status == httpClient.HTTP_OK) {
					friendlist = new ArrayList<User>();
					String output = httpResponse.getBodyAsString();
					try {
						JSONArray friend = new JSONArray(output);
						for (int i = 0; i < friend.getJSONArray(0).length(); i++) {
							int user_id;
							String first_name;
							String last_name;
					
							String email;
							user_id = friend.getJSONArray(0).getJSONObject(i).getInt("user_id");
							first_name = friend.getJSONArray(0).getJSONObject(i).getString("first_name");
							last_name = friend.getJSONArray(0).getJSONObject(i).getString("last_name");
							email = friend.getJSONArray(0).getJSONObject(i).getString("email");
							User userFriend = new User();
							userFriend.setUserid(user_id);
							userFriend.setFirst_name(first_name);
							userFriend.setLast_name(last_name);
							userFriend.setEmail(email);
							String output2 = first_name + " " + last_name;
							
							HashMap<String, String> curChildMap = new HashMap<String, String>();
							childData.get(1).add(curChildMap);
							curChildMap.put(CHILD, output2);
							friendlist.add(userFriend);
							lfapp.saveFriendlist(friendlist);
							group_check_states.add(false);
						}

						//showToast("Friends Added From DB!");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					showToast("Cannot get friendlist. Please try again.");
				}
			}

			@Override
			public void onError(Exception e) {
				showToast("Server error. Please try again.");
				e.printStackTrace();
			}
		});
	}
	
	private void getARFriendList(String email) {
		final LifeSyncHttpClient httpClient = new LifeSyncHttpClient();
		ParameterMap params = httpClient.newParams();

		params.add("email", email);

		// Contact server using POST via separate thread
		httpClient.get("/friendlist", params, new AsyncCallback() {
			@Override
			public void onComplete(HttpResponse httpResponse) {
				int status = httpResponse.getStatus();

				if (status == httpClient.HTTP_OK) {

					ARfriendlist = new ArrayList<User>();

					String output = httpResponse.getBodyAsString();
					try {
						JSONArray friend = new JSONArray(output);
						for (int i = 0; i < friend.getJSONArray(1).length(); i++) {
							int user_id;
							String first_name;
							String last_name;
					
							String email;
							user_id = friend.getJSONArray(1).getJSONObject(i).getInt("user_id");
							first_name = friend.getJSONArray(1).getJSONObject(i).getString("first_name");
							last_name = friend.getJSONArray(1).getJSONObject(i).getString("last_name");
							email = friend.getJSONArray(1).getJSONObject(i).getString("email");
							User userFriend = new User();
							userFriend.setUserid(user_id);
							userFriend.setFirst_name(first_name);
							userFriend.setLast_name(last_name);
							userFriend.setEmail(email);
							String output2 = first_name + " " + last_name;
							
							HashMap<String, String> curChildMap = new HashMap<String, String>();
							childData.get(2).add(curChildMap);
							curChildMap.put(CHILD, output2);
							ARfriendlist.add(userFriend);
						}
						
						showToast("ARFriends Added From DB!");
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					showToast("Cannot get ARfriendlist. Please try again.");
				}
			}

			@Override
			public void onError(Exception e) {
				showToast("Server error. Please try again.");
				e.printStackTrace();
			}
		});
	}
	
	/*This method will add a friend on pending list by specifying the email*/
	private void addNewFriend(String friendemail) {
		final LifeSyncHttpClient httpClient = new LifeSyncHttpClient();
		ParameterMap params = httpClient.newParams();

		params.add("email", user.getEmail());
		params.add("friendemail", friendemail);

		// Contact server using POST via separate thread
		httpClient.get("/friendAdd/new", params, new AsyncCallback() {
			@Override
			public void onComplete(HttpResponse httpResponse) {
				int status = httpResponse.getStatus();

				if (status == httpClient.HTTP_OK) {
					// Retrieve httpResponse as a string
					String output = httpResponse.getBodyAsString();
					try {
						String friendname = "";
						//Create a new friend array via JSON
						JSONArray friend = new JSONArray(output);
						for (int i = 0; i < friend.length(); i++) {
							int user_id;
							String first_name;
							String last_name;
					
							String email;
							user_id = friend.getJSONObject(i).getInt("user_id");
							first_name = friend.getJSONObject(i).getString("first_name");
							last_name = friend.getJSONObject(i).getString("last_name");
							email = friend.getJSONObject(i).getString("email");
							User userFriend = new User();
							userFriend.setUserid(user_id);
							userFriend.setFirst_name(first_name);
							userFriend.setLast_name(last_name);
							userFriend.setEmail(email);
							friendname = first_name + " " + last_name;
							
							friendlist.add(userFriend);
						}						
						showToast("Added New Friend:" + friendname);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					showToast("Cannot add new friend. Please try again.");
				}
			}

			@Override
			public void onError(Exception e) {
				showToast("Server error. Please try again.");
				e.printStackTrace();
			}
		});
	}
	
	private void addFriendviaBump(String friendemail) {
		final LifeSyncHttpClient httpClient = new LifeSyncHttpClient();
		ParameterMap params = httpClient.newParams();

		params.add("email", user.getEmail());
		params.add("friendemail", friendemail);

		// Contact server using POST via separate thread
		httpClient.get("/friendAdd", params, new AsyncCallback() {
			@Override
			public void onComplete(HttpResponse httpResponse) {
				int status = httpResponse.getStatus();

				if (status == httpClient.HTTP_OK) {

					// Retrieve httpResponse as a string
					String output = httpResponse.getBodyAsString();
					try {
						String friendname = "";
						//Create a new friend array via JSON
						JSONArray friend = new JSONArray(output);
						for (int i = 0; i < friend.length(); i++) {
							int user_id;
							String first_name;
							String last_name;
							String email;
							user_id = friend.getJSONObject(i).getInt("user_id");
							first_name = friend.getJSONObject(i).getString("first_name");
							last_name = friend.getJSONObject(i).getString("last_name");
							email = friend.getJSONObject(i).getString("email");
							User userFriend = new User();
							userFriend.setUserid(user_id);
							userFriend.setFirst_name(first_name);
							userFriend.setLast_name(last_name);
							userFriend.setEmail(email);
							friendname = first_name + " " + last_name;
							
							friendlist.add(userFriend);
						}						
						showToast("Added New Friend:" + friendname);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					showToast("Cannot add new friend. Please bump again.");
				}
			}

			@Override
			public void onError(Exception e) {
				showToast("Server error. Please try again later.");
				e.printStackTrace();
			}
		});
	}
	/*This method will approve a friend on pending list by specifying the email*/
	private void approveFriend(String friendemail) {
		final LifeSyncHttpClient httpClient = new LifeSyncHttpClient();
		ParameterMap params = httpClient.newParams();


		params.add("email", user.getEmail());
		params.add("friendemail", friendemail);


		// Contact server using POST via separate thread
		httpClient.get("/friendApprove", params, new AsyncCallback() {
			@Override
			public void onComplete(HttpResponse httpResponse) {
				int status = httpResponse.getStatus();
			
				if (status ==  httpClient.HTTP_OK) {
					
					// Retrieve httpResponse as a string
					String output = httpResponse.getBodyAsString();
					try {
						String friendname ="";
						//Create a new friend array via JSON
						JSONArray friend = new JSONArray(output);
						for (int i = 0; i < friend.length(); i++) {
							int user_id;
							String first_name;
							String last_name;
					
							String email;
							user_id = friend.getJSONObject(i).getInt("user_id");
							first_name = friend.getJSONObject(i).getString("first_name");
							last_name = friend.getJSONObject(i).getString("last_name");
							email = friend.getJSONObject(i).getString("email");
							User userFriend = new User();
							userFriend.setUserid(user_id);
							userFriend.setFirst_name(first_name);
							userFriend.setLast_name(last_name);
							userFriend.setEmail(email);
						}
						showToast( "Approving Friend:" + friendname );
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					showToast("Cannot approve friend. Please try again.");
				}
	
			}

			@Override
			public void onError(Exception e) {
				showToast("Server error. Please try again.");
				e.printStackTrace();
			}
		});
	}
	/*This method will delete a friend on pending list by specifying the email*/
	private void deleteFriend(String friendemail) {
		final LifeSyncHttpClient httpClient = new LifeSyncHttpClient();
		ParameterMap params = httpClient.newParams();


		params.add("email", user.getEmail());
		params.add("friendemail", friendemail);


		// Contact server using POST via separate thread
		httpClient.get("/friendDelete", params, new AsyncCallback() {
			@Override
			public void onComplete(HttpResponse httpResponse) {
				int status = httpResponse.getStatus();
			
				if (status ==  httpClient.HTTP_OK) {
					
					// Retrieve httpResponse as a string
					String output = httpResponse.getBodyAsString();
					try {
						String friendname ="";
						//Create a new friend array via JSON
						JSONArray friend = new JSONArray(output);
						for (int i = 0; i < friend.length(); i++) {
							int user_id;
							String first_name;
							String last_name;
					
							String email;
							user_id = friend.getJSONObject(i).getInt("user_id");
							first_name = friend.getJSONObject(i).getString("first_name");
							last_name = friend.getJSONObject(i).getString("last_name");
							email = friend.getJSONObject(i).getString("email");
							User userFriend = new User();
							userFriend.setUserid(user_id);
							userFriend.setFirst_name(first_name);
							userFriend.setLast_name(last_name);
							userFriend.setEmail(email);
						}
						showToast( "Deleting Friend:" + friendname );
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					showToast("Cannot approve friend. Please try again.");
				}
	
			}

			@Override
			public void onError(Exception e) {
				showToast("Server error. Please try again.");
				e.printStackTrace();
			}
		});
	}
	
	private void getScheduleEventList(User queryuser) {
		final LifeSyncHttpClient httpClient = new LifeSyncHttpClient();
		ParameterMap params = httpClient.newParams();

		params.add("email", queryuser.getEmail());
		
		final int queryuser_id = queryuser.getUserid();

		// Contact server using POST via separate thread
		httpClient.get("/schedule", params, new AsyncCallback() {
			@Override
			public void onComplete(HttpResponse httpResponse) {
				int status = httpResponse.getStatus();

				if (status == httpClient.HTTP_OK) {
					ArrayList<ScheduleEvent> eventList= new ArrayList<ScheduleEvent>();
					
					try {
						JSONArray event = new JSONArray(httpResponse.getBodyAsString());

						for (int i = 0; i < event.length(); i++) {
							int event_id;
							String event_name;
							String event_start_time;
							String event_end_time;
							String event_location;
							String event_description;
							
							event_id = event.getJSONObject(i).getInt("event_id");
							event_name = event.getJSONObject(i).getString("name");
							event_start_time = event.getJSONObject(i).getString("start_time");
							event_end_time = event.getJSONObject(i).getString("end_time");
							event_location = event.getJSONObject(i).getString("location");
							event_description = event.getJSONObject(i).getString("description");
							
							SimpleDateFormat inFomatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
							SimpleDateFormat outFormatter = new SimpleDateFormat("EEE-HH");
							String get_time[];
							
							HashMap<String, String> day_of_week_map = new HashMap<String, String>();
							day_of_week_map.put("Sun", "0");
							day_of_week_map.put("Mon", "1");
							day_of_week_map.put("Tue", "2");
							day_of_week_map.put("Wed", "3");
							day_of_week_map.put("Thu", "4");
							day_of_week_map.put("Fri", "5");
							day_of_week_map.put("Sat", "6");

							Date date = inFomatter.parse(event_start_time);
							get_time = outFormatter.format(date).split("-");
							event_start_time = day_of_week_map.get(get_time[0]) + "-" + get_time[1];
							
							date = inFomatter.parse(event_end_time);
							get_time = outFormatter.format(date).split("-");
							event_end_time = day_of_week_map.get(get_time[0]) + "-" + get_time[1];
							
							ScheduleEvent se = new ScheduleEvent(event_name, event_start_time, event_end_time, event_location, event_description, queryuser_id, event_id);
							schedule_data.put(se.getEvent_id(), se);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					lfapp.saveSchedule(schedule_data);
					update_time_slots_data();
					schedule_adapter.notifyDataSetChanged();
					
				} else {
					showToast("Cannot get schedule. Please try again.");
				}
			}

			@Override
			public void onError(Exception e) {
				//showToast("Server error. Please try again.");
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
						
						/*Testing Bump*/
						//addFriendviaBump(friend);
						
						addNewFriend(friend);
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
						approveFriend(pendfriendlist.get(childPosition).getEmail());

						HashMap<String, String> curChildMap = new HashMap<String, String>();
						childData.get(1).add(curChildMap);
						curChildMap.put(
								CHILD,
								(String) mChildData.get(groupPosition)
										.get(childPosition).get(mChildFrom[1]));
						childData.get(0).remove(childPosition);
						
						friendlist.add(pendfriendlist.get(childPosition));
						pendfriendlist.remove(childPosition);
						group_check_states.add(false);
						lfapp.saveFriendlist(friendlist);		
						
						
						mAdapter.notifyDataSetChanged();
						
						showToast("New Friend Added!");
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

								if (viewHolder1.checkbox.isChecked()) {
									group_check_states.set(childPosition, true);

									getScheduleEventList(friendlist.get(childPosition));
									
								} else {
									group_check_states
											.set(childPosition, false);
									
	
									removeFriendSchedule(friendlist.get(childPosition));
								}

		
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
				
				view.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						
						// LOGIC TO REMOVE FRIEND
						AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
						builder.setCancelable(true);
						builder.setMessage("Delete Friend?");
						builder.setInverseBackgroundForced(true);
						builder.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								});
						builder.setPositiveButton("Delete",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										deleteFriend(friendlist.get(childPosition).getEmail());
										HashMap<String, String> curChildMap = new HashMap<String, String>();
										childData.get(1).remove(childPosition);
										group_check_states.remove(childPosition);
										
										removeFriendSchedule(friendlist.get(childPosition));
										friendlist.remove(childPosition);
										lfapp.saveFriendlist(friendlist);
										mAdapter.notifyDataSetChanged();
										
										// showToast("Friend removed.");
									}
								});
						AlertDialog alert = builder.create();
						alert.show();
						return true;
					}
				});
				
				break;

			case 2:
				LayoutInflater inflater2 = getLayoutInflater();
				view = inflater2.inflate(
						R.layout.row_layout_3, null);
				final ViewHolder viewHolder2 = new ViewHolder();
				viewHolder2.text = (TextView) view
						.findViewById(R.id.label3);
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
				int eventID = Integer.parseInt( event_data.get("event_id") );
				ScheduleEvent se = new ScheduleEvent(
						event_data.get("event_name"),
						event_data.get("event_start_time"),
						event_data.get("event_end_time"),
						event_data.get("event_location"),
						event_data.get("event_description"),
						user.getUserid(),
						eventID );

				schedule_data.put(eventID, se);
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
		update_time_slots_data();
		schedule_adapter.notifyDataSetChanged();
	}

	public void update_time_slots_data() {
		time_slots_data.clear();
		for (int i = 0; i < 24; i++) {
			ArrayList<TimeSlot> time_slots_by_time = new ArrayList<TimeSlot>();
			for (int j = 0; j < 7; j++) {
				time_slots_by_time.add(new TimeSlot(0));
			}
			time_slots_data.add(time_slots_by_time);
		}
		
		if (schedule_data.size() > 0) {

			for (ScheduleEvent se : schedule_data.values()) {
				
				int status = 1;
				
				if (se.getEvent_owner() != user.getUserid()) {
					status = 2;
				}
				
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
							time_slots_data.get(j).get(i).setStatus(status);
							time_slots_data.get(j).get(i).addEvent(se);
						}
					}
					continue;
				}

				// If starttime is greater than endttime then start from monday-00:00 to endtime
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
							time_slots_data.get(j).get(i).setStatus(status);
							time_slots_data.get(j).get(i).addEvent(se);
						}

						// SPECIAL CASE FOR 23:00-00:00
						if (i < event_end_time[0]) {
							time_slots_data.get(23).get(i).setStatus(status);
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
						time_slots_data.get(j).get(i).setStatus(status);
						time_slots_data.get(j).get(i).addEvent(se);
					}

					// SPECIAL CASE FOR 23:00-00:00
					if (i < event_end_time[0]) {
						time_slots_data.get(23).get(i).setStatus(status);
						time_slots_data.get(23).get(i).addEvent(se);
					}
				}

				if (sunday_midnight) {
					time_slots_data.get(23).get(6).setStatus(status);
					time_slots_data.get(23).get(6).addEvent(se);
				}
			}
		}
	}
	
	public void removeFriendSchedule(User friend_user) {
		if (schedule_data.size() > 0) {
			Iterator it = schedule_data.entrySet().iterator();
			while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry) it.next();
		        if (((ScheduleEvent) pairs.getValue()).getEvent_owner() == friend_user.getUserid()) {
		        	it.remove();
		        }
		    }
		}
		
		lfapp.saveSchedule(schedule_data);
		update_time_slots_data();
		schedule_adapter.notifyDataSetChanged();
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
									// Remove reference to currently logged in user
									user = null; 
									schedule_data.clear();
									lfapp.saveSchedule(schedule_data);
									time_slots_data.clear();
									for (int i = 0; i < 24; i++) {
										ArrayList<TimeSlot> time_slots_by_time = new ArrayList<TimeSlot>();
										for (int j = 0; j < 7; j++) {
											time_slots_by_time.add(new TimeSlot(0));
										}
										time_slots_data.add(time_slots_by_time);
									}
									
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
