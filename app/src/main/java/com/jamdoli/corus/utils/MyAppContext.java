package com.jamdoli.corus.utils;

import android.content.Context;


public class MyAppContext {
    private static Context mContext = null;

    private static MyAppContext myInstance = null;

    private MyAppContext() {

    }


    public static Context getInstance() {
        if (myInstance == null) {
            myInstance = new MyAppContext();
        }

       // Log.d("getContext", "stored context ..." + myInstance.mContext.getClass().getName());
        return myInstance.mContext;
    }
    public static void setInstance(String callerName, Context context) {
        if (myInstance == null) {
            myInstance = new MyAppContext();
        }
        if (context == null) {
          //  Log.d("setContext", "callerName: " + callerName + ", Setting context ...null");
        } else {
          //  Log.d("setContext", "callerName: " + callerName + ", Setting context ..." + context.getPackageName());
        }
        myInstance.mContext = context;
    }



}
