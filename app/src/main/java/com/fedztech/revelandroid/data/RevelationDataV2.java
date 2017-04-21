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

import android.util.Log;

import com.fedztech.revelandroid.R;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class RevelationDataV2 extends RevelationDataBase implements RevelationDataIf {
    private byte[] decryptedXML;


    public byte[] addPaddingToCompressedData(byte[] inputArray){

        int inputArrayLength = inputArray.length;
        int numOctets = 16 - (inputArray.length % 16);
        byte octetValue = 0x00;
        if(numOctets == 0){
            numOctets = 16;
        }
        octetValue = (byte) numOctets;


        byte[] outputArray = new byte[inputArrayLength + numOctets];
        System.arraycopy(inputArray, 0, outputArray, 0, inputArrayLength);
        for (int i = 0; i < numOctets; i++) {
            outputArray[inputArrayLength + i] = octetValue;
        }

        return outputArray;
    }

    @Override
    public void processEncryptedData(byte[] password, byte[] rawData) throws RevelationData_Exception{
        final int saltLength = 8;
        final int ivLength = 16;
        byte[] salt = new byte[saltLength];
        byte[] iv = new byte[ivLength];

        int retVal = R.string.error_NoError;

        System.arraycopy(rawData, 12, salt, 0, saltLength);
        System.arraycopy(rawData, 20, iv, 0, ivLength);


        /*
//https://github.com/MarmaladeSky/aRevelation


                byte[] input = Arrays.copyOfRange(fileData, 36, fileData.length);
                byte[] compressedData = cypher.doFinal(input);

                byte[] hash256 = Arrays.copyOfRange(compressedData, 0, 32);

                compressedData = Arrays.copyOfRange(compressedData, 32, compressedData.length);

                compressedData = addPadding(compressedData);

                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(compressedData);
                byte[] computedHash = md.digest();

                if(!Arrays.equals(computedHash, hash256)) {
                    throw new Exception("Invalid data");
                }

                byte[] result = decompress(compressedData);
                return new String(result, Charset.forName("UTF-8"));

         */

        Key key = null;
        Cipher cipher = null;
        try {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toString().toCharArray(), salt, 12000, 256);
            SecretKey secretKey = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(pbeKeySpec);
            key = new SecretKeySpec(secretKey.getEncoded(),"AES");
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        }catch(NoSuchAlgorithmException|InvalidKeySpecException|NoSuchPaddingException ex){
            Log.e(getClass().getName(), "Cryptography functions not supported." + ex.toString());
            ex.printStackTrace();
        }


        try {
            if(cipher != null && key != null) {

                cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            }
            else{
                throw new InternalError("Could not generate cryptographic parameters.");
            }
        } catch (InvalidKeyException|InvalidAlgorithmParameterException e) {

            e.printStackTrace();
        }

        byte[] restData = new byte[rawData.length-36];
        System.arraycopy(rawData,36,restData,0,rawData.length-36);

        byte[] decryptedButCompressed = new byte[rawData.length-36];
        try {
            decryptedButCompressed = cipher.doFinal(restData);
        } catch (IllegalBlockSizeException|BadPaddingException e) {
            throw new RevelationData_Exception(R.string.error_File_Invalid_Format, "Failed to decrypt data.",e);
        }

        // Check the password
        byte[] hash256 = new byte[32];
        System.arraycopy(decryptedButCompressed, 0, hash256, 0, 32);
        System.arraycopy(decryptedButCompressed, 32, decryptedButCompressed, 0, decryptedButCompressed.length - 32);
        decryptedButCompressed = addPaddingToCompressedData(decryptedButCompressed);

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(decryptedButCompressed, 0, decryptedButCompressed.length - 32);
            if(!Arrays.equals(hash256,md.digest())){
                throw new RevelationData_Exception(R.string.error_InvalidPassword,"The read data is invalid.",null);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
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

    }
}
