package com.jamdoli.corus.utils;

import android.util.Log;
import com.jamdoli.corus.BuildConfig;


public class DebugLogManager {

    private static DebugLogManager instance;

    public DebugLogManager(){

    }
    public static DebugLogManager getInstance(){

        if (instance == null){
            instance=new DebugLogManager();
        }
        return instance;
    }


    public void  logsForDebugging(String tag, String logMsg){
        if (BuildConfig.DEBUG){
            Log.d(tag,logMsg);
        }
    }

    public void logsForError(String tag, String logMsg){
//        if (BuildConfig.DEBUG){
            Log.e(tag,logMsg);
//        }
    }

    public void logsForRelease(String tag, String logMsg){
        if (!BuildConfig.DEBUG){
            Log.e(tag,logMsg);
        }
    }

}
