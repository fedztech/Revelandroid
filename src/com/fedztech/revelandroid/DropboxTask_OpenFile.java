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

import java.io.IOException;

import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

public class DropboxTask_OpenFile extends AsyncTask<DropboxTaskParams_OpenFile, Void, Long>
{
	
	 protected Long doInBackground(DropboxTaskParams_OpenFile... params) {
 		
 		try {
 		    DropboxFileInfo info = params[0].api.getFile(params[0].fileName,"",params[0].data,params[0].progress);
 		    Log.i("DbExampleLog", "The downloaded file's rev is: " + info.getMetadata().rev);
 		    params[0].progress.setProgress(1.0);
 		   
 		} catch (DropboxUnlinkedException e) {
 		    // User has unlinked, ask them to link again here.
 		    Log.e("DbExampleLog", "User has unlinked.");
 		} catch (DropboxException e) {
 		    Log.e("DbExampleLog", "Something went wrong while uploading.");
 		} catch (Exception e){
 			Log.e("DbExampleLog", "Wrong");
 		} finally {
 		    if (params[0].data != null) {
 		        try {
 		        	params[0].data.close();
 		        } catch (IOException e) {}
 		    }
 		} 
 	
 		return 0L;
     }

     protected void onProgressUpdate(Integer... progress) {
         //setProgressPercent(progress[0]);
     }

     protected void onPostExecute(Long result) {
         //showDialog("Downloaded " + result + " bytes");
     }

	
};