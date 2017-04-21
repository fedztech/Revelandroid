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

import java.util.List;

import com.fedztech.revelandroid.RevelationData.Entry;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EntriesAdapter extends ArrayAdapter<Entry> {

	   	Context context; 
	    int layoutResourceId; 
	    List<Entry> data = null;
	  
	    
	    public EntriesAdapter(Context context, int layoutResourceId, List<Entry> data) {
	        super(context, layoutResourceId, data);
	        this.layoutResourceId = layoutResourceId;
	        this.context = context;
	        this.data = data;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        EntryHolder holder = null;
	        
	        if(row == null)
	        {
	            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	            row = inflater.inflate(layoutResourceId, parent, false);
	            
	            holder = new EntryHolder();
	            holder.imgIcon = (ImageView)row.findViewById(R.id.entryRowIcon);
	            holder.txtTitle = (TextView)row.findViewById(R.id.entryRowTitle);
	            
	            row.setTag(holder);
	        }
	        else
	        {
	            holder = (EntryHolder)row.getTag();
	        }
	        
	        Entry entry = data.get(position);
	        holder.txtTitle.setText(entry.name);
	        
	        if(entry.type.compareTo("folder") == 0){
	        	holder.imgIcon.setImageResource(R.mipmap.ic_list_folder);
	        }
	        else{
	        	holder.imgIcon.setImageResource(R.mipmap.ic_launcher);
	        }
	        
	        
	        
	        return row;
	    }
	    
	    static class EntryHolder
	    {
	        ImageView imgIcon;
	        TextView txtTitle;
	    }

}
