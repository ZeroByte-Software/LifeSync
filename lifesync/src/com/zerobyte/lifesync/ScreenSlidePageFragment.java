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

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 * <p>This class is used by the {@link CardFlipActivity} and {@link
 * EventDisplayActivity} samples.</p>
 */
public class ScreenSlidePageFragment extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

    
    private LifeSyncApplication lfapp;
	HashMap<Integer, ScheduleEvent> schedule_data = null;
	private ArrayList<Integer> schedule_id_list;
	
	 /**
	 * Factory method for this fragment class. Constructs a new fragment for the given page number.
	 */
	public static ScreenSlidePageFragment create(int pageNumber) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        
        lfapp = (LifeSyncApplication) getActivity().getApplication();
		schedule_data = new HashMap<Integer, ScheduleEvent>(lfapp.getSchedule());
		schedule_id_list = ((EventDisplayActivity) getActivity()).get_schedule_id_list();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.event_page, container, false);

        // Set the title view to show the page number.
//        ((TextView) rootView.findViewById(android.R.id.text1)).setText(
//                mPageNumber + 1);
        
        
        TextView pageNumber = (TextView) rootView.findViewById(R.id.pageNumber);
		int pos = mPageNumber + 1;
		pageNumber.setText("Event " + pos + " of "
				+ schedule_id_list.size());
		
		ScheduleEvent se = schedule_data.get(schedule_id_list.get(mPageNumber));
		
		TextView eventName = (TextView) rootView.findViewById(R.id.eventName);
		TextView eventLocation = (TextView) rootView.findViewById(R.id.eventLocation);
		TextView eventDescription = (TextView) rootView.findViewById(R.id.eventDescription);
		TextView eventOwner = (TextView) rootView.findViewById(R.id.eventOwner);
		
		eventName.setText(se.getEvent_name());
		eventLocation.setText(se.getEvent_location());
		eventDescription.setText(se.getEvent_description());
		eventOwner.setText(se.getEvent_owner());
		
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
		
		TextView eventStart = (TextView) rootView.findViewById(R.id.eventStart);
		TextView eventEnd = (TextView) rootView.findViewById(R.id.eventEnd);
		eventStart.setText(day_of_week[event_start_time[0]] + " " + time_of_day[event_start_time[1]]);
		eventEnd.setText(day_of_week[event_end_time[0]] + " " + time_of_day[event_end_time[1]]);
		

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
