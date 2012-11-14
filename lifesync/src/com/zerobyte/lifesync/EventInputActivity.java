package com.zerobyte.lifesync;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class EventInputActivity extends Activity implements
		OnItemSelectedListener {

	private Toast toast;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eventinput);

		Spinner s_start_day = (Spinner) findViewById(R.id.event_start_day_spinner);
		Spinner s_start_time = (Spinner) findViewById(R.id.event_start_time_spinner);
		Spinner s_end_day = (Spinner) findViewById(R.id.event_end_day_spinner);
		Spinner s_end_time = (Spinner) findViewById(R.id.event_end_time_spinner);

		ArrayAdapter<CharSequence> start_day_adapter = ArrayAdapter
				.createFromResource(this, R.array.day_of_week,
						android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		start_day_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		s_start_day.setAdapter(start_day_adapter);
		s_start_day.setOnItemSelectedListener(this);

		ArrayAdapter<CharSequence> start_time_adapter = ArrayAdapter
				.createFromResource(this, R.array.time_of_day,
						android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		start_time_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		s_start_time.setAdapter(start_time_adapter);
		s_start_time.setOnItemSelectedListener(this);

		ArrayAdapter<CharSequence> end_day_adapter = ArrayAdapter
				.createFromResource(this, R.array.day_of_week,
						android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		end_day_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		s_end_day.setAdapter(end_day_adapter);
		s_end_day.setOnItemSelectedListener(this);

		ArrayAdapter<CharSequence> end_time_adapter = ArrayAdapter
				.createFromResource(this, R.array.time_of_day,
						android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		end_time_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		s_end_time.setAdapter(end_time_adapter);
		s_end_time.setOnItemSelectedListener(this);
		
		
		
		
		// Toast setup
		toast = toast.makeText(getApplicationContext(), "Event end time is not after start time.", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		new MenuInflater(this).inflate(R.menu.menu_eventinput, menu);

		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.OK_option:
			int start_day_pos = ((Spinner) findViewById(R.id.event_start_day_spinner))
					.getSelectedItemPosition();
			int start_time_pos = ((Spinner) findViewById(R.id.event_start_time_spinner))
					.getSelectedItemPosition();
			int end_day_pos = ((Spinner) findViewById(R.id.event_end_day_spinner))
					.getSelectedItemPosition();
			int end_time_pos = ((Spinner) findViewById(R.id.event_end_time_spinner))
					.getSelectedItemPosition();
			
			System.out.println("HAHAHA: " + start_day_pos);
			System.out.println("LOL: " + start_time_pos);

			if (start_day_pos > end_day_pos) {
				toast.show();
				break;
			} else {
				if(start_day_pos == end_day_pos) {
					if(start_time_pos >= end_time_pos) {
						toast.show();
						break;
					}
				}
			}

			HashMap<String, String> event_data = new HashMap<String, String>();
			event_data.put("event_name",
					((EditText) findViewById(R.id.event_name)).getText().toString());
			event_data.put("event_start_time", start_day_pos + "-" + start_time_pos);
			event_data.put("event_end_time", end_day_pos + "-" + end_time_pos);
			event_data.put("event_location", ((EditText) findViewById(R.id.event_location)).getText()
							.toString());
			event_data.put("event_description", ((EditText) findViewById(R.id.event_description)).getText()
							.toString());

			Intent resultIntent = new Intent();
			resultIntent.putExtra("event_data", event_data);
			setResult(RESULT_OK, resultIntent);
			finish();
			break;

		case R.id.CANCEL_option:
			// LOGIC TO PROMPT FOR EMAIL WITH DIALOG TO ADD FRIEND
			setResult(RESULT_CANCELED);
			finish();
			break;

		default:
			return super.onOptionsItemSelected(item);

		}

		return true;
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// An item was selected. You can retrieve the selected item using
		// parent.getItemAtPosition(pos)
		switch (parent.getId()) {
		// case R.id.event_start_day_spinner
		}
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}

}
