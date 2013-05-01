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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Fragment_SelectProvider extends Fragment {
	
	
	private OnProviderSelectedListener mCallback = null;
	private ListView providersList = null;
	
	/**
	 * Interface where we inform the main activity which data provider
	 * (e.g. Dropbox) the user decided to use to open the passwords
	 * file.
	 */
    public interface OnProviderSelectedListener {
        public void onProviderSelected(Providers.EProviders provider);
    }	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            //mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }
        
		View selectProviderView = null;
		selectProviderView = inflater.inflate(R.layout.fragment_select_provider, container, false);
		
		if(selectProviderView != null){
			providersList =  (ListView)selectProviderView.findViewById(R.id.listProviders);
		}
		
		if(providersList != null){
			
			List<String> valuesList = new ArrayList<String>();
			Providers theProviders = new Providers();
			for(Providers.EProviders provider : Providers.EProviders.values()){
				//Local is currently not supported.
				if(provider != Providers.EProviders.PRO_LOCAL){
					valuesList.add(theProviders.getProviderString(provider));
				}
			}
			String[] values = new String[ valuesList.size() ];
			valuesList.toArray( values );			
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
											  android.R.layout.simple_list_item_1, 
											  android.R.id.text1, 
											  values);
	
			providersList.setAdapter(adapter);
			
			providersList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
					if(mCallback != null){
						String providerText = (String) ((TextView)arg0.getChildAt(position)).getText();
						Providers theProviders = new Providers();
						mCallback.onProviderSelected(theProviders.getProviderId(providerText));
					}
				}
			});
		}

        return selectProviderView;
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnProviderSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProviderSelectedListener");
        }
    }    
}
