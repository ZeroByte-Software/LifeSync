/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zerobyte.lifesync;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.zerobyte.lifesync.model.User;

/**
 * Demonstrates a "screen-slide" animation using a {@link ViewPager}. Because
 * {@link ViewPager} automatically plays such an animation when calling
 * {@link ViewPager#setCurrentItem(int)}, there isn't any animation-specific
 * code in this sample.
 * 
 * <p>
 * This sample shows a "next" button that advances the user to the next step in
 * a wizard, animating the current screen out (to the left) and the next screen
 * in (from the right). The reverse animation is played when the user presses
 * the "previous" button.
 * </p>
 * 
 * @see ScreenSlidePageFragment
 */
public class EventDisplayActivity extends FragmentActivity {
	/**
	 * The pager widget, which handles animation and allows swiping horizontally
	 * to access previous and next wizard steps.
	 */
	private ViewPager mPager;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private PagerAdapter mPagerAdapter;
	
	
	private LifeSyncApplication lfapp;
	private User user;
	HashMap<Integer, ScheduleEvent> schedule_data;
	private ArrayList<Integer> schedule_id_list;
	
	private static final int EDIT_EVENT = 0;
	
	private boolean goback = false;
	private boolean is_own_event = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eventdisplay);

		// Get handler to Application
		lfapp = (LifeSyncApplication) getApplication();
		user = new User(lfapp.getUser());
		schedule_data = new HashMap<Integer, ScheduleEvent>(lfapp.getSchedule());

		schedule_id_list = getIntent()
				.getIntegerArrayListExtra("events_viewed");

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		((ScreenSlidePagerAdapter) mPagerAdapter)
				.set_num_events(schedule_id_list.size());
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When changing pages, reset the action bar actions since they
				// are dependent
				// on which page is currently active. An alternative approach is
				// to have each
				// fragment expose actions itself (rather than the activity
				// exposing actions),
				// but for simplicity, the activity provides the actions in this
				// sample.
				invalidateOptionsMenu();

				TextView pageNumber = (TextView) findViewById(R.id.pageNumber);
				int pos = position + 1;
				pageNumber.setText("Event " + pos + " of " + mPagerAdapter.getCount());

				ScheduleEvent se = schedule_data.get(schedule_id_list
						.get(position));

				TextView eventName = (TextView) findViewById(R.id.eventName);
				TextView eventLocation = (TextView) findViewById(R.id.eventLocation);
				TextView eventDescription = (TextView) findViewById(R.id.eventDescription);
				TextView eventOwner = (TextView) findViewById(R.id.eventOwner);

				eventName.setText(se.getEvent_name());
				eventLocation.setText(se.getEvent_location());
				eventDescription.setText(se.getEvent_description());
				if(se.getEvent_owner() == user.getUserid()) {
					eventOwner.setText("Self");
					is_own_event = true;
				} else {
					eventOwner.setText("Other");
					
					for (User u: lfapp.getFriendlist()) {
						if (u.getUserid() == se.getEvent_owner()) {
							eventOwner.setText(u.getFirst_name() + " " + u.getLast_name());
							break;
						}
					}
//					eventOwner.setText(se.getEvent_owner());
					is_own_event = false;
				}
				invalidateOptionsMenu();
				
				String event_start_time_str[] = se.getEvent_start_time().split("-");
				String event_end_time_str[] = se.getEvent_end_time().split("-");
				int[] event_start_time = new int[2];
				int[] event_end_time = new int[2];
				event_start_time[0] = Integer.parseInt(event_start_time_str[0]);
				event_start_time[1] = Integer.parseInt(event_start_time_str[1]);
				event_end_time[0] = Integer.parseInt(event_end_time_str[0]);
				event_end_time[1] = Integer.parseInt(event_end_time_str[1]);
				
				String[] day_of_week = getResources().getStringArray(R.array.day_of_week);
				String [] time_of_day = getResources().getStringArray(R.array.time_of_day);
				
				TextView eventStart = (TextView) findViewById(R.id.eventStart);
				TextView eventEnd = (TextView) findViewById(R.id.eventEnd);
				eventStart.setText(day_of_week[event_start_time[0]] + " " + time_of_day[event_start_time[1]]);
				eventEnd.setText(day_of_week[event_end_time[0]] + " " + time_of_day[event_end_time[1]]);
				
			}
		});
		
		if( user.getUserid() != schedule_data.get(schedule_id_list.get(0)).getEvent_owner()) {
			is_own_event = false;
			invalidateOptionsMenu();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		if (is_own_event) {
			getMenuInflater().inflate(R.menu.menu_eventdisplay, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.editEvent_option:
			// LOGIC TO EDIT EVENT
			Intent EditEventIntent = new Intent(EventDisplayActivity.this,
					EventInputActivity.class);
			int currentPage = mPager.getCurrentItem();
			ScheduleEvent se = schedule_data.get(schedule_id_list.get(currentPage));

			HashMap<String, String> event_data = new HashMap<String, String>();
			event_data.put( "event_id", "" + se.getEvent_id() );
			event_data.put("event_name", se.getEvent_name());
			event_data.put("event_start_time", se.getEvent_start_time());
			event_data.put("event_end_time", se.getEvent_end_time());
			event_data.put("event_location", se.getEvent_location());
			event_data.put("event_description", se.getEvent_description());
			EditEventIntent.putExtra("event_data", event_data);
			startActivityForResult(EditEventIntent, EDIT_EVENT);

			return true;

		case R.id.deleteEvent_option:
			// LOGIC TO DELETE EVENT
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(true);
			builder.setMessage("Delete Event?");
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
							final LifeSyncHttpClient httpClient = new LifeSyncHttpClient();
							ParameterMap params = httpClient.newParams();
							final int currentPage = mPager.getCurrentItem();
							ScheduleEvent se = schedule_data.get(schedule_id_list.get(currentPage));
							int eventID = se.getEvent_id();
							
							params.add("event_id", "" + eventID);
							
							httpClient.post("/removeEvent", params, new AsyncCallback() {
								@Override
								public void onComplete(HttpResponse httpResponse) {
									int status = httpResponse.getStatus();
				
									if (status == httpClient.HTTP_OK)
									{
										showToast( "Event successfully removed!");
										schedule_data.remove(schedule_id_list.get(currentPage));
										lfapp.saveSchedule(schedule_data);
										onBackPressed();
									}
									else {
										showToast("Error removing event.");
									}
								}
				
								@Override
								public void onError(Exception e) {
									showToast( "Sorry, a server error occurred. Please try again. " );
									showToast( e.getMessage() );
									e.printStackTrace();
								}
							});
							
							
							// schedule_id_list.remove(currentPage);
							//mPager.removeViewAt(currentPage);

						}
					});
			AlertDialog alert = builder.create();
			alert.show();

			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			if (requestCode == EDIT_EVENT) {
				HashMap<String, String> event_data = (HashMap<String, String>) data
						.getSerializableExtra("event_data");
				ScheduleEvent se = schedule_data.get(Integer.parseInt(event_data.get("event_id")));
				se.setEvent_name(event_data.get("event_name"));
				se.setEvent_start_time(event_data.get("event_start_time"));
				se.setEvent_end_time(event_data.get("event_end_time"));
				se.setEvent_location(event_data.get("event_location"));
				se.setEvent_description(event_data.get("event_description"));
			}

			lfapp.saveSchedule(schedule_data);
			goback = true;
		}

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(goback) {
			onBackPressed();
		}
	}
	

	/**
	 * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment}
	 * objects, in sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

		private int num_events;

		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return ScreenSlidePageFragment.create(position);
		}

		@Override
		public int getCount() {
			return num_events;
		}

		public void set_num_events(int num_events) {
			this.num_events = num_events;
		}
	}

	// Pass to for fragment
	public ArrayList<Integer> get_schedule_id_list() {
		return schedule_id_list;
	}
	
	private void showToast(String text) {
		Toast toast = Toast.makeText(getApplicationContext(),
				"", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.setText(text);
		
		toast.show();
	}
}
