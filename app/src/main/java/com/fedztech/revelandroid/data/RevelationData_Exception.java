package com.fedztech.revelandroid.data;

/**
 * Created by fedz on 12/7/15.
 */
public class RevelationData_Exception extends Exception {

    private final int theCode;

    public RevelationData_Exception(int code, String detail, Throwable cause){
        super(detail, cause);
        theCode = code;
    }

    public int getCode(){
        return theCode;
    }

}
