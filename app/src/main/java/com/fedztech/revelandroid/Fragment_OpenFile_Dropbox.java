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

import java.io.ByteArrayOutputStream;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.fedztech.revelandroid.data.RevelationData;
import com.fedztech.revelandroid.data.RevelationDataBase;
import com.fedztech.revelandroid.data.RevelationDataFactory;
import com.fedztech.revelandroid.data.RevelationData_Exception;

public class Fragment_OpenFile_Dropbox extends Fragment implements OnClickListener{
    OnOpenFileListener mCallback;
    
	final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    // The app key is replaced by the one given by dropbox
    final static private String APP_KEY = "xxxxxxxxxxxxxxx";
    // the app secret is replaced by the one given by dropbox
	final static private String APP_SECRET = "xxxxxxxxxxxxxxx";
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	private DropboxAPI<AndroidAuthSession> mDBApi;
	private boolean isLoggedIn;
	Spinner fileListSpinner = null;
	EditText passwordField = null;
	boolean fieldsInitialized = false;
	ArrayAdapter<String> filesAdapter = null;
	TextView errorText = null;
	Button openButton = null;
	
	
	
    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnOpenFileListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onOpenFile(RevelationDataBase position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            //TODO: Save/Restore state
        }
        
		AndroidAuthSession session = buildSession();
		mDBApi = new DropboxAPI<AndroidAuthSession>(session); 
		
        // Inflate the layout for this fragment
		View openFileView = null;
		openFileView = inflater.inflate(R.layout.fragment_openfile_dropbox, container, false);		
		
		openButton = (Button) openFileView.findViewById(R.id.buttonOpen);
		if(openButton != null){
			openButton.setOnClickListener(this);
		}
		
		View view = openFileView.findViewById(R.id.fileChoicesSpinner);
		
		if(view != null){
			fileListSpinner = (Spinner)view;
		}
		
		view = openFileView.findViewById(R.id.errorText);
		if(view != null){
			errorText = (TextView)view;
		}
		
		filesAdapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_dropdown_item_1line, android.R.id.text1);
		
		view = openFileView.findViewById(R.id.editTextPassword);
		if(view != null){
			passwordField = (EditText)view;
		}
		
		if(mDBApi != null){
			boolean isLoggedIn = mDBApi.getSession().isLinked();
	    	if(!isLoggedIn){
				try{
					mDBApi.getSession().startAuthentication(getActivity());
				}
				catch(IllegalStateException ex){
					errorText.setText(ex.getLocalizedMessage());
				}
	    	}
	    	else{
	    		initializeFields();
	    	}			
		}
		else{
			errorText.setText(R.string.error_Dropbox_Connect);
		}
		
        return openFileView;
    }
    
    private void resetFields(){
    	fieldsInitialized = false;
    }
    
    private void initializeFields(){
    	
    	if(fieldsInitialized == false){
    		if(fileListSpinner != null)
			{
				
				DropboxTaskParams_Metadata params = new DropboxTaskParams_Metadata();
				params.data = null;
				params.api = mDBApi;
				params.progress = new DropboxListener_OpenFileProgress();
				params.progress.onProgress(0L, 1L);
				new DropboxTask_GetFileList().execute(params);
				
				

				while(params.progress.getProgress() < 1.0){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// Ignore
					}
				}
				
				if(params.data != null && filesAdapter!= null){
					if(params.data.contents.size()>0){
						for(int ix= 0; ix < params.data.contents.size(); ix++){
							filesAdapter.add(params.data.contents.get(ix).fileName());
						}
						if(errorText != null){
							errorText.setText(R.string.instr_Dropbox_OpenFile);
						}
					}
					else{
						if(errorText !=null){
							errorText.setText(R.string.error_Dropbox_NoFiles);
						}
					}
				}
				
				if(filesAdapter != null){
					filesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					if(fileListSpinner != null && filesAdapter.getCount()>0){
						fileListSpinner.setAdapter(filesAdapter);
					}
					else{
						if(fileListSpinner != null){
							fileListSpinner.setEnabled(false);
						}
						if(passwordField != null){
							passwordField.setEnabled(false);
						}
						if(openButton != null){
							openButton.setClickable(false);
						}
						
						errorText.setText(R.string.error_Dropbox_NoFiles);
					}
				}
				
				fieldsInitialized = true;
			}			
    	}
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnOpenFileListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnOpenFileListener");
        }
    }
    
	public void onResume() {
	    super.onResume();

	    if (mDBApi.getSession().authenticationSuccessful()) {
	        try {
	            mDBApi.getSession().finishAuthentication();
	            AccessTokenPair tokens = mDBApi.getSession().getAccessTokenPair();
	            storeKeys(tokens.key, tokens.secret);
	            initializeFields();
	        } catch (IllegalStateException e) {
	            Log.i("DbAuthLog", "Error authenticating", e);
	        }
	    }
	}
	
    private void storeKeys(String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }
	
	
    private String[] getKeys() {
        SharedPreferences prefs = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
        	String[] ret = new String[2];
        	ret[0] = key;
        	ret[1] = secret;
        	return ret;
        } else {
        	return null;
        }
    }	
    
    @SuppressWarnings("unused")
	private void clearKeys() {
        SharedPreferences prefs = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }
	
    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonOpen:
			openAndDecodeFile();
		}
	}
	
	public void openAndDecodeFile()
	{
		//Parameter Validation
		int passwordLength = passwordField.getText().length();
		if(passwordLength == 0 || passwordLength > 32){
			new DialogFragment_Alert(R.string.error_InvalidPassword).show(getFragmentManager(), "");
			return;
		}
		
		String selectedFileName = "";
		if(fileListSpinner.getSelectedItem()!= null){
			selectedFileName = "/" + fileListSpinner.getSelectedItem().toString();
		}
		
		if(selectedFileName.length()<=1){
			new DialogFragment_Alert(R.string.error_InvalidFileChosen).show(getFragmentManager(), "");
			return;
		}
		
		isLoggedIn = false;
		
		if(mDBApi != null){
			isLoggedIn = mDBApi.getSession().isLinked();
		}
		
    	if(!isLoggedIn){
    		mDBApi.getSession().startAuthentication(getActivity());
    	}
    	else{
    		
    		errorText.setText(R.string.instr_Dropbox_Opening);
    		
    		DropboxTaskParams_OpenFile openFileParams = new DropboxTaskParams_OpenFile();
    		openFileParams.data = new ByteArrayOutputStream();
    		openFileParams.api = mDBApi;
    		openFileParams.progress = new DropboxListener_OpenFileProgress();
    		openFileParams.fileName = selectedFileName;
    		

    		if(errorText != null){
    			errorText.setText(R.string.instr_Dropbox_Opening);
    		}
    		
			try {
    		    new DropboxTask_OpenFile().execute(openFileParams);
    		    while(openFileParams.progress.getProgress()<1.0){
    		    	Thread.sleep(10);
    		    }
    		    
                byte[] password = new byte[32];
                if(passwordField != null){
                    
                   
                	for(int ix=0; ix < passwordField.getText().length(); ix++){
					    password[ix] = (byte) passwordField.getText().charAt(ix);
                	}   
                	
                    for(int ix = passwordLength; ix< 32; ix++){
                    	password[ix] = 0x00;
                    }
                    
                    passwordField.setText("");
                }

                if(openFileParams.data.size() > 0){
					mCallback.onOpenFile(RevelationDataFactory.getRevelationData(openFileParams.data.toByteArray(), password));
                }
                else{
                	new DialogFragment_Alert(R.string.error_File_Empty).show(getFragmentManager(), "");
                }
                  
                resetFields();
    		    
    		}  catch (RevelationData_Exception e) {
				new DialogFragment_Alert(e.getCode()).show(getFragmentManager(), "");
			}catch(Exception e){
				new DialogFragment_Alert(R.string.error_Unknown).show(getFragmentManager(), "");
    		} finally {
    		   
    		}  	
    			  		
    	}	
	}
}
