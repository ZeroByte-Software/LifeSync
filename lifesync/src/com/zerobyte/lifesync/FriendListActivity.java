package com.zerobyte.lifesync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
public class FriendListActivity extends ExpandableListActivity {
	private static final String NAME = "NAME";     
	private static final String IS_EVEN = "IS_EVEN"; 
	private SimpleExpandableListAdapter mAdapter;
	
	@Override   
    public void onCreate(Bundle savedInstanceState)   
    {   
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.activity_friend_list);   
        
        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String,String>>>();
        for (int i = 0; i < 3; i++){
        	Map<String, String> curGroupMap = new HashMap<String, String>();
        	groupData.add(curGroupMap);
        	switch (i){
        		case 0: {curGroupMap.put(NAME,  "Friend Requests");break;}
        		case 1: {curGroupMap.put(NAME,  "Friends");break;}
        		case 2: {curGroupMap.put(NAME,  "Friends Awaiting");break;}
        	}
        	
        	List<Map<String, String>> children = new ArrayList<Map<String, String>>();
        	for (int j = 0; j < 5; j++){
        		Map<String, String> curChildMap = new HashMap<String, String>();
        		children.add(curChildMap);
        		curChildMap.put(IS_EVEN, (j % 2 == 0)? "Eddy " + j: "Eton " + j);
        	}
        	childData.add(children);
        }
        //Setup adapter
        mAdapter = new SimpleExpandableListAdapter( 
        		this, 
        		groupData, // groupData describes the first-level entries 
        		android.R.layout.simple_expandable_list_item_1, // Layout for the first-level entries new
        		new String[] {NAME, IS_EVEN}, // Key in the groupData maps to display 
        		new int[] { android.R.id.text1, android.R.id.text2 }, // Data under "colorName" key goes into this TextView 
        		childData, // childData describes second-level entries 
        		android.R.layout.simple_expandable_list_item_2, // Layout for second-level entries 
        		new String[] { NAME, IS_EVEN }, // Keys in childData maps to display 
        		new int[] { android.R.id.text1, android.R.id.text2} // Data under the keys above go into these TextViews 
        		);
        setListAdapter(mAdapter);
        
        ExpandableListView listView = (ExpandableListView) findViewById(android.R.id.list);
        listView.expandGroup(1); // Friends
    }   
}
