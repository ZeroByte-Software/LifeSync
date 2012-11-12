package com.zerobyte.lifesync;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ScheduleActivity extends Activity {

	private ListView listView;
	private ScheduleListAdapter adapter;
	boolean init_flag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);

		List<ArrayList<ScheduleEvent>> schedule_data = new ArrayList<ArrayList<ScheduleEvent>>();

		if (!init_flag) {
			for (int i = 0; i < 24; i++) {
				ArrayList<ScheduleEvent> time_slots = new ArrayList<ScheduleEvent>();
				for (int j = 0; j < 7; j++) {
					Random random = new Random();
					boolean rand_bool = random.nextBoolean();
					time_slots.add(new ScheduleEvent(rand_bool));
				}
				schedule_data.add(time_slots);
			}
		}

		init_flag = true;

		adapter = new ScheduleListAdapter(this, schedule_data);

		listView = (ListView) findViewById(R.id.schedule_list);
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
		listView.setAdapter(adapter);

	}
}
