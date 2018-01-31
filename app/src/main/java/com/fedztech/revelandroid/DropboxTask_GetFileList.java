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

//import com.dropbox.client2.exception.DropboxException;
//import com.dropbox.client2.exception.DropboxUnlinkedException;

import android.os.AsyncTask;
import android.util.Log;
/*
public class DropboxTask_GetFileList extends AsyncTask<DropboxTaskParams_Metadata, Void, Long>{

	com.dropbox.client2.DropboxAPI.Entry metadata = null;
	
	@Override
	protected Long doInBackground(DropboxTaskParams_Metadata... params) {
 		try {
 			params[0].progress.onProgress(0L, 1L);
 		    params[0].data = params[0].api.metadata("/", 100, null, true, null);
 		    params[0].progress.onProgress(1L, 1L);
 		    
 		} catch (DropboxUnlinkedException e) {
 		    // User has unlinked, ask them to link again here.
 		    Log.e("DbExampleLog", "User has unlinked.");
 		} catch (DropboxException e) {
 		    Log.e("DbExampleLog", "Something went wrong while uploading.");
 		} catch (Exception e){
 			Log.e("DbExampleLog", "Wrong");
 		} finally {
 		   
 		} 
 		
 		return 0L;
	}
	
    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Long result) {
        //showDialog("Downloaded " + result + " bytes");
    }
    
    com.dropbox.client2.DropboxAPI.Entry getFiles()
    {
    	return metadata;
    }

}
*/