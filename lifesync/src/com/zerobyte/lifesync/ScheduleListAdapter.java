package com.zerobyte.lifesync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ScheduleListAdapter extends ArrayAdapter<ArrayList<ScheduleEvent>> {

	LifeSyncApplication lfapp;
	
	private Context context;
	private HashMap<Integer, ScheduleEvent> schedule_data;
	private List<ArrayList<TimeSlot>> time_slots_data;

	static class ViewHolder {
		public TextView time_slot_tv;
		public Button mon_btn, tue_btn, wed_btn, thu_btn, fri_btn, sat_btn,
				sun_btn;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ScheduleListAdapter(Context context, List time_slots_data, LifeSyncApplication lfapp) {
		super(context, R.layout.schedule_row, time_slots_data);
		this.context = context;
		this.time_slots_data = time_slots_data;
		this.lfapp = lfapp;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View rowView = convertView;
		ViewHolder holder = null;

		if (rowView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			rowView = inflater.inflate(R.layout.schedule_row, parent, false);

			holder = new ViewHolder();

			holder.time_slot_tv = (TextView) rowView
					.findViewById(R.id.time_slot);

			holder.mon_btn = (Button) rowView.findViewById(R.id.mon_slot);
			holder.tue_btn = (Button) rowView.findViewById(R.id.tue_slot);
			holder.wed_btn = (Button) rowView.findViewById(R.id.wed_slot);
			holder.thu_btn = (Button) rowView.findViewById(R.id.thu_slot);
			holder.fri_btn = (Button) rowView.findViewById(R.id.fri_slot);
			holder.sat_btn = (Button) rowView.findViewById(R.id.sat_slot);
			holder.sun_btn = (Button) rowView.findViewById(R.id.sun_slot);

			rowView.setTag(holder);
		} else {
			holder = (ViewHolder) rowView.getTag();
		}

		ArrayList<TimeSlot> time_slots_by_time = time_slots_data.get(position);

		String time_label = position + ":00";
		if (position < 10) {
			time_label = "0" + time_label;
		}
		holder.time_slot_tv.setText(time_label);

		Resources rsc = getContext().getResources();
		int AndroidGreen = rsc.getColor(android.R.color.holo_green_dark);
		int AndroidBlue = rsc.getColor(android.R.color.holo_blue_dark);
		int AndroidRed = rsc.getColor(android.R.color.holo_red_dark);
		int BlankWhite = Color.parseColor("#EEEEEE");

		for (int i = 0; i < 7; i++) {
			final int pos = position;
			final int ind = i;

			Button day_btn = null;
			switch (i) {
			case 0:
				day_btn = holder.mon_btn;
				break;

			case 1:
				day_btn = holder.tue_btn;
				break;

			case 2:
				day_btn = holder.wed_btn;
				break;

			case 3:
				day_btn = holder.thu_btn;
				break;

			case 4:
				day_btn = holder.fri_btn;
				break;

			case 5:
				day_btn = holder.sat_btn;
				break;

			case 6:
				day_btn = holder.sun_btn;
				break;
			}

			int slot_color = time_slots_by_time.get(i).getStatus();
			if (slot_color == 2) {
				schedule_data = new HashMap<Integer, ScheduleEvent>(lfapp.getSchedule());
				for (Integer ei: time_slots_by_time.get(i).getEventIds()) {
					if (schedule_data.get(ei).getEvent_owner() == lfapp.getUser().getUserid())
					{
						slot_color = 3;
						break;
					}
				}
					
			}
			
			switch (slot_color) {
			case 0: // Empty
//				day_btn.setBackgroundColor(android.R.drawable.btn_default);
				day_btn.setBackgroundColor(BlankWhite);
//				day_btn.getBackground().setColorFilter(Color.parseColor("#DDDDDD"), PorterDuff.Mode.DARKEN);
				break;

			case 1: // Self
				day_btn.setBackgroundColor(AndroidGreen);
				break;

			case 2: // Self
				day_btn.setBackgroundColor(AndroidBlue);
				break;
				
			default: // Else
				day_btn.setBackgroundColor(AndroidRed);
				break;
			}

			if (time_slots_data.get(pos).get(ind).getStatus() != 0) {
				day_btn.setText(String.valueOf(time_slots_data.get(pos).get(ind).getEventIds()
						.size()));
			} else {
				day_btn.setText("");
			}

			day_btn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (time_slots_data.get(pos).get(ind).getStatus() != 0) {
						Intent displayEventIntent = new Intent(v.getContext(),
								EventDisplayActivity.class);
						displayEventIntent
								.putIntegerArrayListExtra("events_viewed",
										time_slots_data.get(pos).get(ind)
												.getEventIds());
						System.out.println(time_slots_data.get(pos).get(ind)
								.getEventIds());
						((AndroidTabLayoutActivity) v.getContext())
								.startActivity(displayEventIntent);
					}
				}

			});

		}

		return rowView;
	}

	public void update_schedule_data(
			HashMap<Integer, ScheduleEvent> schedule_data) {
		this.schedule_data = schedule_data;

		notifyDataSetChanged();
	}
}
