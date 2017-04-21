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

import android.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import com.fedztech.revelandroid.data.RevelationData;

public class Fragment_Display_Entry extends Fragment {
	
	
	RevelationData.Entry theEntry;
	
	public Fragment_Display_Entry(){
		theEntry = null;
	}
	
	public void setEntry(RevelationData.Entry entry){
		theEntry = entry;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		
        if (savedInstanceState != null) {
            //mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }
        
       
        View entriesView = null;
		entriesView = inflater.inflate(R.layout.fragment_display_entry, container, false);
		
		GridLayout layout = (GridLayout)entriesView.findViewById(R.id.displayEntryGrid);
		
		TextView txt = new TextView(layout.getContext());
		txt.setText(getString(R.string.field_name));
		layout.addView(txt);
		
		EditText edit = new EditText(layout.getContext());
		edit.setFocusable(false);
		edit.setText(theEntry.name);
		layout.addView(edit);
		
		txt = new TextView(layout.getContext());
		txt.setText(getString(R.string.field_description));
		layout.addView(txt);
		
		edit = new EditText(layout.getContext());
		edit.setFocusable(false);
		edit.setText(theEntry.description);
		layout.addView(edit);
		
		for(int ix = 0; ix < theEntry.fields.size(); ix++)
		{
			txt = new TextView(layout.getContext());
			txt.setText(getDisplayText(theEntry.fields.get(ix).fieldId));
			layout.addView(txt);
			
			
			edit = new EditText(layout.getContext());
			edit.setText(theEntry.fields.get(ix).fieldValue);
			edit.setFocusable(false);
			layout.addView(edit);	
			
			if(theEntry.fields.get(ix).fieldId.compareTo("generic-password") == 0 ||
					theEntry.fields.get(ix).fieldId.compareTo("creditcard-ccv") == 0 || 
					theEntry.fields.get(ix).fieldId.compareTo("generic-pin") == 0
					)
			{
				edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				edit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						EditText edit = (EditText)v;
						if( (edit.getInputType() & InputType.TYPE_TEXT_VARIATION_PASSWORD) != 0)
						{
							edit.setInputType(InputType.TYPE_CLASS_TEXT);
						}
						else
						{
							edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
						}
						
					}
				});
			}
		

		}
		return entriesView;
	}
	
	private String getDisplayText(String codeName){
		String retVal = "";

		if(codeName.compareTo("generic-hostname")==0){
			return getString(R.string.field_generic_hostname);
		}
		if(codeName.compareTo("generic-password")==0){
			return getString(R.string.field_generic_password);
		}
		if(codeName.compareTo("generic-username")==0){
			return getString(R.string.field_generic_username);
		}
		if(codeName.compareTo("generic-url")==0){
			return getString(R.string.field_generic_url);
		}
		if(codeName.compareTo("generic-email")==0){
			return getString(R.string.field_generic_email);
		}
		if(codeName.compareTo("generic-pin")==0){
			return getString(R.string.field_generic_pin);
		}
		if(codeName.compareTo("creditcard-cardtype")==0){
			return getString(R.string.field_creditcard_cardtype);
		}
		if(codeName.compareTo("creditcard-cardnumber")==0){
			return getString(R.string.field_creditcard_cardnumber);
		}
		if(codeName.compareTo("creditcard-expirydate")==0){
			return getString(R.string.field_creditcard_expirydate);
		}
		if(codeName.compareTo("creditcard-ccv")==0){
			return getString(R.string.field_creditcard_ccv);
		}
		
		return retVal;
	}
}
