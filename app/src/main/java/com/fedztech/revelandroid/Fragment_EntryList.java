/*  Revelandroid - An app for the Revelation Password Manager data.
    Copyright (C) 2013  Juan Carlos Garza

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.fedztech.revelandroid;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Fragment_EntryList extends Fragment {
	
	OnEntrySelectedListener mCallback;
	
    public interface OnEntrySelectedListener {
        public void onEntrySelected(RevelationData.Entry entry);
        public void onFolderSelected(RevelationData data, ArrayList<Integer> path);
    }
	
	RevelationData theData;
	EntriesAdapter adapter = null;
	ArrayList<Integer> thePath = null;
	
	
	public Fragment_EntryList() {
		theData = null;
		thePath = null;
	}	
	
	public void setArguments(RevelationData data, ArrayList<Integer> path) {
		theData = data;
		thePath = path;
	}
	

	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            //mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }
        
		View entriesView = null;
		entriesView = inflater.inflate(R.layout.fragment_entry_list, container, false);
		
		if(entriesView != null){
			
		}
		ListView entriesList =  (ListView)entriesView.findViewById(R.id.listEntries);

		
		
		entriesView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if( keyCode == KeyEvent.KEYCODE_BACK )
				{
				    return true;
				}
				return false;
			}
		});
		
		
		
		if(thePath==null){
			thePath = new ArrayList<Integer>();
		}
		
		
		
		if(theData != null){
			List<RevelationData.Entry> currEntries = null;
			if(thePath != null){
				currEntries = theData.getEntries();
				for(int i= 0; i< thePath.size(); i++){
					currEntries = currEntries.get(thePath.get(i)).entries;
				}
			}
			if(currEntries != null){
				//currEntries = theData.getEntries();
			
				adapter = new EntriesAdapter(getActivity(),
						  R.layout.entry_item_row,  
						  currEntries);
				
				if(adapter != null){
					entriesList.setAdapter(adapter);
				}
			}
			else{
				if(theData.getEntries() != null){
					adapter = new EntriesAdapter(getActivity(),R.layout.entry_item_row,  theData.getEntries());
					
					if(adapter != null){
						entriesList.setAdapter(adapter);
					}		
				}
				else{
					Log.e("Fragment_EntryList", "No data!!!");
					//TODO Error handling
				}
			}
		}
		
		
		entriesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				
				List<RevelationData.Entry> currEntries = theData.getEntries();
				if(thePath != null){
					for(int i= 0; i< thePath.size(); i++){
						currEntries = currEntries.get(thePath.get(i)).entries;
					}
				}
				if(currEntries.get(position).type.compareTo("folder") == 0){
					/*
					adapter = new EntriesAdapter(getActivity(),R.layout.entry_item_row,currEntries.get(position).entries);
					if(adapter != null){
						((ListView)arg0).setAdapter(adapter);
					}
					*/
					if(thePath!=null){
						thePath.add(position);
					}
				
					mCallback.onFolderSelected(theData,thePath);
				}
				else{
					mCallback.onEntrySelected(currEntries.get(position));
				}
			}
		});

		
        return entriesView;
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnEntrySelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProviderSelectedListener");
        }
    } 
    
    public void setData(RevelationData data)
    {
    	theData = data;
    }
    
    public void setPath(ArrayList<Integer> path)
    {
    	thePath = path;
    }
    
    public void reducePath()
    {
    	if(thePath.size()>0){
    		thePath.remove(thePath.size()-1);
    	}
    }
    
    public boolean canGoBack(){
    	boolean canGoBack = false;
    	
    	if(thePath == null){
    		canGoBack = true;
    	}
    	else{
    		if(thePath.size()==0){
    			canGoBack = true;
    		}
    	}
    	
    	return canGoBack;
    }
    
    boolean isPaused = false;
    
    @Override
    public void onPause() {
    	super.onPause();
    	isPaused = true;
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	isPaused = false;
    }
    
    public boolean isPaused(){
    	return isPaused;
    }
    
	
	
}
