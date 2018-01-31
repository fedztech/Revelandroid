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

//import com.dropbox.client2.ProgressListener;


public class DropboxListener_OpenFileProgress /* extends ProgressListener */ {

	private double progressPercent = 0.0;
	//@Override
	public void onProgress(long bytes, long total) {
		if(progressPercent < 1.0)
		{
			progressPercent = bytes / total;
		}
		
	}
	
	public double getProgress(){
		return progressPercent;
	}
	
	public void setProgress(double in)
	{
		progressPercent = in;
	}
	

}
