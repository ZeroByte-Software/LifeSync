package com.zerobyte.lifesync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class EventDisplayActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eventdisplay);

		EventPagerAdapter adapter = new EventPagerAdapter();
		ViewPager eventPager = (ViewPager) findViewById(R.id.event_pager);
		eventPager.setAdapter(adapter);
		eventPager.setCurrentItem(0);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

			new MenuInflater(this).inflate(R.menu.menu_eventdisplay, menu);


		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.editEvent_option:
			// LOGIC TO EDIT EVENT
			return true;
		case R.id.deleteEvent_option:
			// LOGIC TO DELETE EVENT
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
