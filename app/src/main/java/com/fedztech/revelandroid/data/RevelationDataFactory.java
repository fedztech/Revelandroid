/*  Revelandroid - An app for the Revelation Password Manager data.
    Copyright (C) 2013-2015  Juan Carlos Garza

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
package com.fedztech.revelandroid.data;


import android.util.Log;

import java.io.IOException;

public class RevelationDataFactory {


    public static RevelationDataBase getRevelationData(byte[] fileByteArray, byte[] password) throws  IOException, RevelationData_Exception{
        RevelationDataBase revelationData = null;
        int version = 0;
        version = RevelationDataBase.getDataVersion(fileByteArray);

        try{
            if(version == RevelationDataTypes.V1.id()){
                RevelationData rdata = null;
                rdata = new RevelationData();
                if(rdata != null){
                    rdata.processEncryptedData(password, fileByteArray);
                    revelationData = rdata;
                }
            }
            else if (version == RevelationDataTypes.V2.id())
            {
                RevelationDataV2 rdata = null;
                rdata = new RevelationDataV2();
                if(rdata != null){
                    rdata.processEncryptedData(password, fileByteArray);
                    revelationData = rdata;
                }
            }
            else{
                throw new IOException("Unsupported version.");
            }
        }
        catch (RevelationData_Exception ex){
            throw ex;
        }


        return revelationData;
    }
}
