package com.zerobyte.lifesync;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class EventDisplayActivity extends Activity {

	private EventPagerAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eventdisplay);

		adapter = new EventPagerAdapter();
		ViewPager eventPager = (ViewPager) findViewById(R.id.event_pager);
		eventPager.setAdapter(adapter);
		eventPager.setCurrentItem(0);


		eventPager.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int pos) {
				// TODO Auto-generated method stub
				TextView pageNumber = (TextView) findViewById(R.id.pageNumber);
				int position = pos + 1;
				pageNumber.setText("Event " + position + " of " + adapter.getCount());
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

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
