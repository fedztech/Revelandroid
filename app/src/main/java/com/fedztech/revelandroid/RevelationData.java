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

/**
 * 
 * This class handles decryption of the Revelation data
 *
 */
public class RevelationData {
	
	private byte[] decryptedXML;
	
	/**
	 * Entries tree
	 */
	private List<Entry> entries = null;
	public List<Entry> getEntries()
	{
		return entries;
	}
	
	/**
	 * Given a password and the raw binary data, it decrypts the data
	 * and stores the tree in the member "entries"
	 * @param password The password to use to decrypt
	 * @param rawData The raw binary data of the Revelation file
	 * @return 0 when successful, other when error.
	 */
	int processEncryptedData(byte[] password, byte[] rawData)
	{
		int retVal = R.string.error_NoError;
		
		//Data Validation
		if(password.length == 0)
		{
			return R.string.error_InvalidPassword;
		}
		
		if(rawData.length == 0)
		{
			return R.string.error_File_Empty;
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
		} catch (NoSuchAlgorithmException e) {
			retVal = R.string.error_Unknown;
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			retVal = R.string.error_Unknown;
			e.printStackTrace();
		}
		
		if(retVal != R.string.error_NoError){
			return retVal;
		}
		
        try {
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		} catch (InvalidKeyException e) {
			retVal = R.string.error_Unknown;
			e.printStackTrace();
		}
        
		if(retVal != R.string.error_NoError){
			return retVal;
		}
       
        byte[]  initialVectorToDecrypt = new byte[16];
        System.arraycopy(rawData,12,initialVectorToDecrypt,0,16);
        byte[] decryptedInitialVector = null;
		try {
			decryptedInitialVector = cipher.doFinal(initialVectorToDecrypt);
		} catch (IllegalBlockSizeException e) {
			retVal = R.string.error_Unknown;
			e.printStackTrace();
		} catch (BadPaddingException e) {
			retVal = R.string.error_Unknown;
			e.printStackTrace();
		}
		
		if(retVal != R.string.error_NoError){
			return retVal;
		}
        
        //Having the initial vector, we re-initialize the cipher to AES, 
        // CBC (Cipher-block chaining) and no Padding. When we initialize, 
		// we use the initial vector.
        try {
			cipher = Cipher.getInstance("AES/CBC/NoPadding");
		} catch (NoSuchAlgorithmException e) {
			retVal = R.string.error_Unknown;
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			retVal = R.string.error_Unknown;
			e.printStackTrace();
		}
        
		if(retVal != R.string.error_NoError){
			return retVal;
		}
        
        try {
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(decryptedInitialVector));
		} catch (InvalidKeyException e) {
			retVal = R.string.error_Unknown;
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			retVal = R.string.error_Unknown;
			e.printStackTrace();
		}
        
		if(retVal != R.string.error_NoError){
			return retVal;
		}
        
        byte[] restData = new byte[rawData.length-28];
        System.arraycopy(rawData,28,restData,0,rawData.length-28);
        
        byte[] decryptedButCompressed = new byte[rawData.length-28];
		try {
			decryptedButCompressed = cipher.doFinal(restData);
		} catch (IllegalBlockSizeException e) {
			retVal = R.string.error_Unknown;
			e.printStackTrace();
		} catch (BadPaddingException e) {
			retVal = R.string.error_Unknown;
			e.printStackTrace();
		}
		
		if(retVal != R.string.error_NoError){
			return retVal;
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
        	retVal = R.string.error_File_Invalid_Format;
            e.printStackTrace();
        }

        inflater.end();
        
		if(retVal != R.string.error_NoError){
			return retVal;
		}
        
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
			retVal = R.string.error_File_Invalid_Format;
			e.printStackTrace();
		} catch (IOException e) {
			retVal = R.string.error_Unknown;
			e.printStackTrace();
		}
        
		
		return retVal;
	}
	
	/**
	 * Represents a field of an entry.
	 *
	 */
	public static class Field {
		public final String fieldId;
		public final String fieldValue;
		private Field(String id, String value){
			fieldId = id;
			fieldValue = value;
		}
	};
	
	/**
	 * The basic node of the tree. 
	 * An entry can contain sub entries and so on.
	 */
	public static class Entry {
	    public String name;
	    public String notes;
	    public String description;
	    public String updated;
	    public String type;
	    public ArrayList<Field> fields;
	    public ArrayList<Entry> entries;
	    
	    private Entry() {
	        fields = new ArrayList<Field>();
	        entries = new ArrayList<Entry>();
	    }
	}	
	
	/**
	 * Holds an empty namepsace
	 */
	private static final String ns = null;	
	
	/**
	 * Given an XmlPullParser, reads the contents of the XML file
	 * and generates a tree of entries.
	 * @param parser The parser to use to generate the tree
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		
		if(entries == null){
			entries = new ArrayList<Entry>();
		}
		else{
			entries.clear();
		}
	   
		// We expect the first tag to be "revelationdata"
		// We recursively read entries
	    parser.require(XmlPullParser.START_TAG, ns, "revelationdata");
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        // Starts by looking for the entry tag
	        if (name.equals("entry")) {
	            entries.add(readEntry(parser));
	        } else {
	            skip(parser);
	        }
	    }  
	}
	
	/**
	 * Recursive function used to generate the tree of entries
	 * @param parser the XmlPullParser to parse the XML data
	 * @return The Entry root node of the current subtree
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, ns, "entry");
	    Entry newEntry = new Entry();
	    for(int i = 0; i< parser.getAttributeCount(); i++){
	    	if(parser.getAttributeName(i).toString().compareTo("type") == 0){
	    		String type = parser.getAttributeValue(i);
	    		if(type != null){
	    			newEntry.type = type;
	    		}
	    		break;
	    	}
	    }
	    
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("name")) {
	            String thename = readName(parser);
	            if(thename != null){
	            	newEntry.name = thename;
	            }
	        } else if (name.equals("notes")) {
	            String notes = readNotes(parser);
	            if(notes != null){
	            	newEntry.notes = notes;
	            }
	        } else if (name.equals("description")) {
	            String description = readDescription(parser);
	            if(description != null){
	            	newEntry.description = description;
	            }
	        } else if (name.equals("updated")) {
	            String updated = readUpdated(parser);
	            if(updated != null){
	            	newEntry.updated = updated;
	            }
	        } else if (name.equals("field")) {
	            Field newField = readField(parser);
	            if(newField != null){
	            	newEntry.fields.add(newField);
	            }
	        } else if (name.equals("entry")) {
	            Entry newSubEntry = readEntry(parser);
	            if(newSubEntry != null){
	            	newEntry.entries.add(newSubEntry);
	            }
	        } else {
	            skip(parser);
	        }
	    }
	    return newEntry;
	}
	
	/**
	 * Reads the name of the entry
	 * @param parser
	 * @return The name of the entry
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "name");
	    String title = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "name");
	    return title;
	}
	
	/**
	 * Reads the notes of the entry
	 * @param parser
	 * @return The notes of the entry
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readNotes(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "notes");
	    String title = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "notes");
	    return title;
	}
	
	/**
	 * Reads the description of the entry
	 * @param parser
	 * @return The description of the entry
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "description");
	    String title = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "description");
	    return title;
	}	
	
	/**
	 * Reads the update date of the entry
	 * @param parser
	 * @return The update date of the entry
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readUpdated(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "updated");
	    String title = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "updated");
	    return title;
	}
	
	/**
	 * Reads the next field of the entry
	 * @param parser
	 * @return The next field of the entry
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private Field readField(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "field");
	    Field retVal = null;
	    String fieldId = null;
	    String fieldValue = null;
	    for(int i = 0; i < parser.getAttributeCount(); i++){
		    String attributeName = parser.getAttributeName(i);
		    if(attributeName.toString().compareTo("id") == 0){
		    	fieldId = parser.getAttributeValue(i);
		    	fieldValue = readText(parser);
		    	break;
		    }    	
	    }
	    retVal = new Field(fieldId, fieldValue);
	    parser.require(XmlPullParser.END_TAG, ns, "field");
	    return retVal;
	}

	/**
	 * Helper function to read text
	 * @param parser
	 * @return The text read
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}
	
	/**
	 * Skips the current element
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
}
