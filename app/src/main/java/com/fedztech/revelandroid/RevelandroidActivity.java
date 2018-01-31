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

import java.io.File;
import java.util.ArrayList;

import com.fedztech.revelandroid.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;

import com.fedztech.revelandroid.data.RevelationDataBase;
/*
 * Main Activity and entry point for the application. 
 * This class does the fragment management.
 */
public class RevelandroidActivity extends Activity 
	implements Fragment_OpenFile_Dropbox.OnOpenFileListener ,  
				Fragment_SelectProvider.OnProviderSelectedListener,
				Fragment_EntryList.OnEntrySelectedListener,
				Fragment_OpenFile_Local.OnOpenLocalFileListener
	{

	private Fragment_SelectProvider firstFragment = null;
	
	public ProgressDialog progress = null;
	
	/**
	 * The first action that is performed is to add the Fragment where the
	 * providers are chosen.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_container);

		
		prepareToRun();
		
		progress = new ProgressDialog(this);
		progress.setMessage("Retrieving data...");
		progress.setIndeterminate(true);
		progress.setCancelable(false);		
		
		
		if (findViewById(R.id.fragment_container) != null) {
			if (savedInstanceState != null) {
                return;
            }
			
			firstFragment = new Fragment_SelectProvider();
			if(firstFragment != null){
				firstFragment.setArguments(getIntent().getExtras());
				getFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
			}
			else{
				//TODO: Inform the user.
			}
		}
	}
	
	private void prepareToRun(){
		//Check that the local directory exists. If it does not, create it.
		String extStorageState = Environment.getExternalStorageState();
		if(extStorageState.compareTo(Environment.MEDIA_MOUNTED) == 0){
			File extStorage =  Environment.getExternalStorageDirectory();
			if(extStorage != null){
				File subdir = new File(extStorage.getAbsolutePath()+"/Revelandroid");
			
				if(subdir != null){
					if(subdir.exists()==false){
						subdir.mkdir();
					}
				}
			}
		}
		else{
			//Do not warn the user yet, as he might open his files with dropbox or other.
		}
	}


	protected void onResume() {
	    super.onResume();
	    //Did I get the dropbox key?
		SharedPreferences prefs = getSharedPreferences("dropbox-settings", Context.MODE_PRIVATE);
		String dbxId = prefs.getString("dropbox-identification", null);
		if(dbxId != null && dbxId.compareTo("1") == 0)
		{
			prefs.edit().putString("dropbox-identification", "0").apply();
			prefs.edit().commit();
			onBackPressed();
		}
	}

	@Override
	public void onOpenFile(RevelationDataBase revelationData) {
		Fragment_EntryList entryListFragment = (Fragment_EntryList)getFragmentManager().findFragmentByTag("theEntryListFragment");
		
		if(entryListFragment == null){
			entryListFragment = new Fragment_EntryList();
			entryListFragment.setArguments(revelationData,null);
		}
		else{
			entryListFragment.setData(revelationData);
			entryListFragment.setPath(null);
		}
		
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, (Fragment)entryListFragment,"theEntryListFragment");
		transaction.addToBackStack(null);
		transaction.commit();	
	}

	@Override
	public void onProviderSelected(Providers.EProviders provider) {
		switch(provider){
			case PRO_LOCAL:{
				Fragment_OpenFile_Local openFileLocalFragment = (Fragment_OpenFile_Local)
						getFragmentManager().findFragmentById(R.layout.fragment_openfile_local);
				
				if(openFileLocalFragment == null){
					openFileLocalFragment = new Fragment_OpenFile_Local();
				}
				
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_container, (Fragment)openFileLocalFragment , "openFileLocalFragment");
				transaction.addToBackStack(null);
				transaction.commit();
				
			};break;
			case PRO_DROPBOX:{
			
				Fragment_OpenFile_Dropbox openFileDropboxFragment = (Fragment_OpenFile_Dropbox)
						getFragmentManager().findFragmentById(R.layout.fragment_openfile_dropbox);
				
				if(openFileDropboxFragment == null){
					openFileDropboxFragment = new Fragment_OpenFile_Dropbox();
				}
				
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_container, (Fragment)openFileDropboxFragment , "openFileDropboxFragment");
				transaction.addToBackStack(null);
				transaction.commit();
			
			};break;
		}
	}


	@Override
	public void onEntrySelected(RevelationDataBase.Entry entry) {
	
		Fragment_Display_Entry displayEntryFragment = (Fragment_Display_Entry)getFragmentManager().findFragmentById(R.layout.fragment_display_entry);
		
		if(displayEntryFragment == null){
			displayEntryFragment = new Fragment_Display_Entry();
			displayEntryFragment.setEntry(entry);
		}
		
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, (Fragment)displayEntryFragment, "displayEntryFragment");
		transaction.addToBackStack(null);
		transaction.commit();		
	}


	@Override
	public void onFolderSelected(RevelationDataBase data, ArrayList<Integer> path) {
		Fragment_EntryList entryListFragment = (Fragment_EntryList)getFragmentManager().findFragmentByTag("theEntryListFragment");
		
		if(entryListFragment == null){
			entryListFragment = new Fragment_EntryList();
			entryListFragment.setArguments(data, path);
		}
		else{
			entryListFragment.setPath(path);
		}
		
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.remove((Fragment)entryListFragment);
		transaction.add(R.id.fragment_container, (Fragment)entryListFragment, "theEntryListFragment");
		//transaction.replace(R.id.fragment_container, (Fragment)entryListFragment, "theEntryListFragment");
		//transaction.addToBackStack(null);
		transaction.commit();	
	}
	
	@Override
	public void onBackPressed() {
		boolean doGoBack = false;
		Fragment_EntryList entryListFragment = (Fragment_EntryList)getFragmentManager().findFragmentByTag("theEntryListFragment");
		if(entryListFragment != null){
			if(entryListFragment.isPaused()==true){
				doGoBack = true;
			}
			else{
				doGoBack = entryListFragment.canGoBack();
				//Swap the fragments then
				if(doGoBack == false){
					FragmentTransaction transaction = getFragmentManager().beginTransaction();
					transaction.remove((Fragment)entryListFragment);
					entryListFragment.reducePath();
					transaction.add(R.id.fragment_container, (Fragment)entryListFragment, "theEntryListFragment");
					transaction.commit();	
				}
			}
		}
		else{
	        doGoBack = true;
		}
		
		if(doGoBack == true){
			super.onBackPressed();
		}
	}


	@Override
	public void onOpenLocalFile(RevelationDataBase rdata) {
		onOpenFile(rdata);
		
	}
	
}