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

package com.fedztech.revelandroid.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.util.Xml;

import com.fedztech.revelandroid.R;

/**
 * 
 * This class handles decryption of the Revelation data
 *
 */
public class RevelationData extends RevelationDataBase implements RevelationDataIf {
	
	private byte[] decryptedXML;
	

	
	/**
	 * Given a password and the raw binary data, it decrypts the data
	 * and stores the tree in the member "entries"
	 * @param password The password to use to decrypt
	 * @param rawData The raw binary data of the Revelation file
	 * @return 0 when successful, other when error.
	 */
	public void processEncryptedData(byte[] password, byte[] rawData) throws  RevelationData_Exception
	{
		
		//Data Validation
		if(password.length == 0)
		{
			throw new RevelationData_Exception( R.string.error_InvalidPassword, "", null);
		}
		
		if(rawData.length == 0)
		{
			throw new RevelationData_Exception(R.string.error_File_Empty, "", null);
		}
		
		//The password has to be padded with zeroes in the case that 
		//it is not 32 bytes long.
		byte [] paddedPassword = new byte[32];
		for(int ix = 0; ix< 32; ix++)
		{
			if(ix < password.length)
			{
				paddedPassword[ix] = password[ix];
			}
			else
			{
				paddedPassword[ix] = 0x00;
			}
		}
		
		//First, we need to decrypt the initial vector which 
		// is composed of the 16 bytes starting from index 12.
		// We create a secret key with the padded password
		SecretKeySpec skeySpec = new SecretKeySpec(password, "AES");
		
		//The cipher to decrypt is AES., ECB (Electronic Code Book) 
		// is used (as we don't have an initialization vector) and we use No Padding. 
		// Then we initialize the cipher in decrypt mode and with the key.
        Cipher cipher = null;
        
		try {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
			throw new RevelationData_Exception(R.string.error_Unknown, "", e);
		}
		
        try {
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			throw new RevelationData_Exception(R.string.error_Unknown,"",e);
		}
       
        byte[]  initialVectorToDecrypt = new byte[16];
        System.arraycopy(rawData,12,initialVectorToDecrypt,0,16);
        byte[] decryptedInitialVector = null;
		try {
			decryptedInitialVector = cipher.doFinal(initialVectorToDecrypt);
		} catch (IllegalBlockSizeException|BadPaddingException e) {
			e.printStackTrace();
			throw new RevelationData_Exception(R.string.error_Unknown, "", e);
		}

        //Having the initial vector, we re-initialize the cipher to AES, 
        // CBC (Cipher-block chaining) and no Padding. When we initialize, 
		// we use the initial vector.
        try {
			cipher = Cipher.getInstance("AES/CBC/NoPadding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
			throw new RevelationData_Exception(R.string.error_Unknown, "", e);
		}
        
        try {
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(decryptedInitialVector));
		} catch (InvalidKeyException |InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			throw new RevelationData_Exception(R.string.error_Unknown, "", e);
		}
        
        byte[] restData = new byte[rawData.length-28];
        System.arraycopy(rawData,28,restData,0,rawData.length-28);
        
        byte[] decryptedButCompressed = new byte[rawData.length-28];
		try {
			decryptedButCompressed = cipher.doFinal(restData);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			throw new RevelationData_Exception(R.string.error_Unknown, "", e);
		}
		
		// The data is now decrypted, yet compressed. The Inflater class
		// is used to do the decompression.
        int numberOfBytesToDecompress = decryptedButCompressed.length;
        Inflater inflater = null;
        inflater = new Inflater();
        inflater.setInput(decryptedButCompressed,0,numberOfBytesToDecompress);

        int compressionFactorMaxLikely = 10;

        int bufferSizeInBytes = numberOfBytesToDecompress * compressionFactorMaxLikely;

        byte[] bytesDecompressed = new byte[bufferSizeInBytes];

        try
        {
            int numberOfBytesAfterDecompression = inflater.inflate(bytesDecompressed);

            decryptedXML = new byte[numberOfBytesAfterDecompression];

            System.arraycopy(bytesDecompressed,0,decryptedXML,0,numberOfBytesAfterDecompression);            
        }
        catch (DataFormatException e)
        {
			e.printStackTrace();
        	throw new RevelationData_Exception(R.string.error_InvalidPassword,"",e);
        }

        inflater.end();
        
        // The data is decrypted and inflated. Now we have an XML file  to parse
        // the readFeed function parses the XML and builds the entries tree.
        ByteArrayInputStream decryptedXMLIS = new ByteArrayInputStream(decryptedXML);
        
        XmlPullParser parser = Xml.newPullParser();
        try {
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(decryptedXMLIS,null);
			parser.nextTag();
			readFeed(parser);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			throw new RevelationData_Exception(R.string.error_File_Invalid_Format,"",e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RevelationData_Exception(R.string.error_Unknown, "", e);
		}
	}
}
