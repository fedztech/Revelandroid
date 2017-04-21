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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.fedztech.revelandroid.data.RevelationData;
import com.fedztech.revelandroid.data.RevelationDataBase;
import com.fedztech.revelandroid.data.RevelationDataFactory;
import com.fedztech.revelandroid.data.RevelationDataTypes;
import com.fedztech.revelandroid.data.RevelationDataV2;
import com.fedztech.revelandroid.data.RevelationData_Exception;

public class Fragment_OpenFile_Local extends Fragment implements OnClickListener {
	OnOpenLocalFileListener mCallback;
	Spinner fileListSpinner = null;
	EditText passwordField = null;
	boolean fieldsInitialized = false;
	ArrayAdapter<String> filesAdapter = null;
	TextView errorText = null;
	Button openButton = null;
	
    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnOpenLocalFileListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onOpenLocalFile(RevelationDataBase position);
    }
	
	 @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            //TODO
        }
       
        // Inflate the layout for this fragment
		View openFileView = null;
		openFileView = inflater.inflate(R.layout.fragment_openfile_local, container, false);		
		
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
			errorText.setText(R.string.empty);
		}
		
		filesAdapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_dropdown_item_1line, android.R.id.text1);
		
		view = openFileView.findViewById(R.id.editTextPassword);
		if(view != null){
			passwordField = (EditText)view;
		}
		
		initializeFields();
		
        return openFileView;
    }	
	
	 private void resetFields(){
	    	fieldsInitialized = false;
	 }
	    
    private void initializeFields(){
    	
    	if(fieldsInitialized == false){
    		if(fileListSpinner != null){	
    			
    			String extStorageState = Environment.getExternalStorageState();
    			if(extStorageState.compareTo(Environment.MEDIA_MOUNTED) == 0){
	    			File extStorage =  Environment.getExternalStorageDirectory();
	    			if(extStorage != null){
	    				File subdir = new File(extStorage.getAbsolutePath()+"/Revelandroid");
	    			
	    				if(subdir != null){
	    					boolean subdirCreated = false;
	    					if(subdir.exists()==false){
	    						subdirCreated = subdir.mkdir();
		    					if(subdirCreated == false && errorText != null){
		    						errorText.setText(R.string.error_CouldNotCreateSubdir);
		    					}
	    					}
	    					
	    					if(subdir.exists()==true){
	    						File[] listOfFiles = subdir.listFiles();
	    						if(listOfFiles != null){
	    							if(listOfFiles.length > 0){
	    								for(int ix=0; ix< listOfFiles.length; ix++){
	    									if(listOfFiles[ix].isFile()){
	    										filesAdapter.add(listOfFiles[ix].getName());
	    									}
	    								}
	    							}
	    							else{
	    								errorText.setText(R.string.error_NoFiles);
	    							}
	    						}
	    					}
	    				}
	    			}	
    			}
    			else{
    				errorText.setText(R.string.error_SDCardNotMounted);
    			}
    			
				filesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				if(filesAdapter.getCount()>0){
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

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnOpenLocalFileListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnOpenFileListener");
        }
    }
    
	public void onResume() {
	    super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		byte[] password = new byte[32];
		String fileName = null;
		
		if(fileListSpinner.getSelectedItem()!= null){
			fileName = fileListSpinner.getSelectedItem().toString();
		}
		
		String extStorageState = Environment.getExternalStorageState();
		if(extStorageState.compareTo(Environment.MEDIA_MOUNTED) == 0){
			File extStorage =  Environment.getExternalStorageDirectory();
			if(extStorage != null){
				File subdir = new File(extStorage.getAbsolutePath()+"/Revelandroid");
			
				if(subdir != null){				
					if(subdir.exists()==true){
						fileName = extStorage.getAbsolutePath()+"/Revelandroid/"+ fileName;
					}
				}
			}
		}
		else{
			errorText.setText(R.string.error_SDCardNotMounted);
		}
		
			
		
		
        int passwordLength = passwordField.getText().length();
        if(passwordLength <= 32){
        	for(int ix=0; ix < passwordField.getText().length(); ix++){
			    password[ix] = (byte) passwordField.getText().charAt(ix);
        	}   
        	
            for(int ix = passwordLength; ix< 32; ix++)
            {
            	password[ix] = 0x00;
            }
        }
        
        passwordField.setText("");


        byte[] fileByteArray = null;
        
        File file = new File(fileName);
        if(file != null){
        	if(file.exists() && file.isFile()){
        		FileInputStream is = null;
				try {
					is = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					errorText.setText(R.string.error_FileNotFound);
				}
        		if(is != null){
        			if(file.length() > 0){
	        			fileByteArray = new byte[(int) file.length()];
	        			if( fileByteArray != null ){
	        				try {
								is.read(fileByteArray);
							} catch (IOException e) {
								errorText.setText(e.getMessage());
							}
	        			}	
        			}
        		}
        	}
        	else{
        		errorText.setText(R.string.error_FileNotFound);
        	}
        }
        
        
        if(fileByteArray != null){
			try {
				mCallback.onOpenLocalFile(RevelationDataFactory.getRevelationData(fileByteArray, password));
			}
			catch(RevelationData_Exception ex){
				errorText.setText(ex.getCode());
			}
			catch(IOException ex){
				errorText.setText(ex.getMessage());
			}
        }
        else{
        	errorText.setText(R.string.error_CouldNotOpenFile);
        }

       
        resetFields();
		
	}
	
	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}

}
