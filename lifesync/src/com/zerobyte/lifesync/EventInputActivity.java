package com.zerobyte.lifesync;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

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

public class EventInputActivity extends LifeSyncActivityBase implements
		OnItemSelectedListener {

	private String edit_event_id;
	private boolean inProcess = false;

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
//		toast = Toast.makeText(getApplicationContext(),
//				"Event end time is not after start time.", Toast.LENGTH_SHORT);
//		toast.setGravity(Gravity.BOTTOM, 0, 0);

		HashMap<String, String> event_data = (HashMap<String, String>) getIntent()
				.getSerializableExtra("event_data");
		if (event_data != null) {
			edit_event_id = event_data.get("event_id");

			((EditText) findViewById(R.id.event_name)).setText(event_data
					.get("event_name"));
			((EditText) findViewById(R.id.event_location)).setText(event_data
					.get("event_location"));
			((EditText) findViewById(R.id.event_description))
					.setText(event_data.get("event_description"));

			String event_start_time_str[] = event_data.get("event_start_time")
					.split("-");
			String event_end_time_str[] = event_data.get("event_end_time")
					.split("-");
			int[] event_start_time = new int[2];
			int[] event_end_time = new int[2];
			event_start_time[0] = Integer.parseInt(event_start_time_str[0]);
			event_start_time[1] = Integer.parseInt(event_start_time_str[1]);
			event_end_time[0] = Integer.parseInt(event_end_time_str[0]);
			event_end_time[1] = Integer.parseInt(event_end_time_str[1]);
			((Spinner) findViewById(R.id.event_start_day_spinner))
					.setSelection(event_start_time[0]);
			((Spinner) findViewById(R.id.event_start_time_spinner))
					.setSelection(event_start_time[1]);
			((Spinner) findViewById(R.id.event_end_day_spinner))
					.setSelection(event_end_time[0]);
			((Spinner) findViewById(R.id.event_end_time_spinner))
					.setSelection(event_end_time[1]);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		if (!inProcess) {
			new MenuInflater(this).inflate(R.menu.menu_eventinput, menu);
		}
		

		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.OK_option:
			inProcess = true;
			invalidateOptionsMenu();

			int start_day_pos = ((Spinner) findViewById(R.id.event_start_day_spinner))
					.getSelectedItemPosition();
			int start_time_pos = ((Spinner) findViewById(R.id.event_start_time_spinner))
					.getSelectedItemPosition();
			int end_day_pos = ((Spinner) findViewById(R.id.event_end_day_spinner))
					.getSelectedItemPosition();
			int end_time_pos = ((Spinner) findViewById(R.id.event_end_time_spinner))
					.getSelectedItemPosition();

			final HashMap<String, String> event_data = new HashMap<String, String>();
			event_data.put("event_name",
					((EditText) findViewById(R.id.event_name)).getText()
							.toString());
			event_data.put("event_start_time", start_day_pos + "-"
					+ start_time_pos);
			event_data.put("event_end_time", end_day_pos + "-" + end_time_pos);
			event_data.put("event_location",
					((EditText) findViewById(R.id.event_location)).getText()
							.toString());
			event_data.put("event_description",
					((EditText) findViewById(R.id.event_description)).getText()
							.toString());
			
			if (edit_event_id == null) {
				// ADDING NEW NOT EDITING
				
				final LifeSyncHttpClient httpClient = new LifeSyncHttpClient();
				ParameterMap params = httpClient.newParams();
				
				String startDateTime, endDateTime;
				
				// Use dummy date: Oct. 1, 2012 = Monday, Oct. 2, 2012 = Tuesday, etc.
				startDateTime = "2012-10-0" + (start_day_pos + 1);
				endDateTime = "2012-10-0" + (end_day_pos + 1);
				if( start_time_pos < 10 ) {
					startDateTime += " 0" + start_time_pos + ":00:00";	// Time format: 0x:00:00
				} else {
					startDateTime += start_time_pos + ":00:00";	// Time format: xx:00:00
				}
				if( end_time_pos < 10 ) {
					endDateTime += " 0" + start_time_pos + ":00:00";
				} else {
						endDateTime += start_time_pos + ":00:00";
				}
				
				params.add("user_id", "" + user.getUserid());
				params.add("event_name", ((EditText) findViewById(R.id.event_name)).getText().toString());
				params.add("event_start_time", startDateTime);
				params.add("event_end_time", endDateTime);
				params.add("event_location", ((EditText) findViewById(R.id.event_location)).getText().toString());
				params.add("event_description", ((EditText) findViewById(R.id.event_description)).getText().toString());
				
				// Contact server using POST via separate thread
				httpClient.post("/inputEvent", params, new AsyncCallback() {
					@Override
					public void onComplete(HttpResponse httpResponse) {
						int status = httpResponse.getStatus();
	
						if (status == httpClient.HTTP_CREATED)
						{
							Intent resultIntent = new Intent();
							resultIntent.putExtra("event_data", event_data);
							setResult(RESULT_OK, resultIntent);
							showToast( "Event successfully created!");
							finish();
						}
						else {
							showToast("Error adding event.");
							inProcess = false;
							invalidateOptionsMenu();
						}
					}
	
					@Override
					public void onError(Exception e) {
						showToast( "Server error. Please try again. " + e.getMessage());
						e.printStackTrace();
						inProcess = false;
						invalidateOptionsMenu();
					}
				});
			} else {
				event_data.put("event_id", edit_event_id);
				Intent resultIntent = new Intent();
				resultIntent.putExtra("event_data", event_data);
				setResult(RESULT_OK, resultIntent);
				finish();
			}
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
