package com.jamdoli.corus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class AppSettingsUsingSharedPrefs {
    private static final String LOG_TAG = "AppSettingsSharedPrefs";

    private static final String MY_SHARED_PREFERS_NAME = "corus";
    private static final String PERSISTENCE_KEY_BASE_URL_STR = "baseUrl";

    private static final String PRE_LOGIN_URL = "http://corus19-1.eu-west-1.elasticbeanstalk.com";
    private static final String BASE_URL = "baseUrl";

    private static final String PERSISTENCE_KEY_USER_DATA = "userData";
    //    private static final String PERSISTENCE_KEY_SELECT_LANGUAGE_CODE = "languageCode";
    private static final String PERSISTENCE_KEY_SELECT_LANGUAGE = "language";

    private static final String PERSISTENCE_KEY_PRE_IS_LOGIN = "isLogin";

    private static final String PERSISTENCE_KEY_AUTH_HEADER = "authenticationHeader";
    private static final String PERSISTENCE_KEY_USER_UNIQUE_ID = "uniqueId";

    private static final String PERSISTENCE_KEY_COUNTRY_CODE = "countryCode";
    private static final String PERSISTENCE_KEY_PARANET_COUNTRY_NAME = "countryName";
    private static final String PERSISTENCE_KEY_CONFIGURATION_DATA = "configuration";
    private static final String PERSISTENCE_KEY_UNIQUE_ID = "uniqueId";
    private static final String PERSISTENCE_KEY_USER_PROFILE_DATA = "userProfile";

    private static final String PERSISTENCE_KEY_FB_KEY = "fbKey";
    private static final String PERSISTENCE_KEY_BT_ADDRESS = "btAddress";
    private static final String PERSISTENCE_KEY_FCM_ID = "fcmId";
    private static final String PERSISTENCE_KEY_DISEASE_STATUS = "diseaseContactStatus";
    private static final String PERSISTENCE_KEY_IS_SEND_DETAILS = "sendContactDetails";




    private static SharedPreferences mSharedPrefs = null;

    private static AppSettingsUsingSharedPrefs myInstance = null;

    public static AppSettingsUsingSharedPrefs getInstance() {
        if (myInstance == null) {
            myInstance = new AppSettingsUsingSharedPrefs();
            if (mSharedPrefs == null) {
                // Because app-context yet not initialized; cleanup and return null
                myInstance = null;
            }
        }
        return myInstance;
    }

    private AppSettingsUsingSharedPrefs() {
        DebugLogManager.getInstance().logsForDebugging(LOG_TAG, "AppSettingsUsingSharedPrefs()");
        if (MyAppContext.getInstance() != null) {
            mSharedPrefs = MyAppContext.getInstance().getSharedPreferences(MY_SHARED_PREFERS_NAME, Context.MODE_PRIVATE);
        } else {
            mSharedPrefs = null;
            Log.e(LOG_TAG, "No valid context available ... ");
        }
    }

    private void printDebugStatement(String msg) {
        DebugLogManager.getInstance().logsForDebugging(LOG_TAG, msg);
    }

    private String getStringValueForKey(String keyName, String strDefaultVal) {
        String strValue = strDefaultVal;
        if (mSharedPrefs != null) {
            if (keyName == null || keyName.equals("")) {
                printDebugStatement("setStringValueForKey() - Invalid key");
                return strDefaultVal;
            }
            strValue = mSharedPrefs.getString(keyName, strDefaultVal);
        } else {
            printDebugStatement("getStringValueForKey() - SharedPrefs not initialized");
        }
        printDebugStatement("getStringValueForKey() - keyName: " + keyName + " , strValue - " + strValue);
        return strValue;
    }

    private void setStringValueForKey(String keyName, String strVal) {
        if (mSharedPrefs != null) {
            if (keyName == null || keyName.equals("")) {
                printDebugStatement("setStringValueForKey() - Invalid key");
                return;
            }
            if (strVal != null) {
                SharedPreferences.Editor settingEditor = mSharedPrefs.edit();
                settingEditor.putString(keyName, strVal);
                settingEditor.apply();
                printDebugStatement("setStringValueForKey() - keyName: " + keyName + " , strValue - " + strVal);

            } else {
                printDebugStatement("setStringValueForKey() - Not a valid value: ");
            }
        } else {
            printDebugStatement("setStringValueForKey() - SharedPrefs not initialized");
        }
    }

    public long getLongValueForKey(String keyName, long iDefaultVal) {
        long iValue = iDefaultVal;
        if (keyName == null || keyName.equals("")) {
            printDebugStatement("getIntValueForKey() - Invalid key");
            return -1;
        }
        if (mSharedPrefs != null) {
            iValue = mSharedPrefs.getLong(keyName, iDefaultVal);
        } else {
            printDebugStatement("getIntValueForKey() - SharedPrefs not initialized");
        }
        printDebugStatement("getIntValueForKey() - keyName: " + keyName + " , iValue: " + iValue);
        return iValue;
    }

    private void setLongValueForKey(String keyName, long iValue) {
        if (mSharedPrefs != null) {
            if (keyName == null || keyName.equals("")) {
                printDebugStatement("setIntValueForKey() - Invalid key");
                return;
            }
            if (iValue >= 0) {
                SharedPreferences.Editor settingEditor = mSharedPrefs.edit();
                settingEditor.putLong(keyName, iValue);
                settingEditor.apply();
            } else {
                printDebugStatement("setIntValueForKey() - Not a valid value: " + iValue);
            }
        } else {
            printDebugStatement("setIntValueForKey() - SharedPrefs not initialized");
        }
    }

    public Integer getIntValueForKey(String keyName, Integer iDefaultVal) {
        Integer iValue = iDefaultVal;
        if (keyName == null || keyName.equals("")) {
            printDebugStatement("getIntValueForKey() - Invalid key");
            return -1;
        }
        if (mSharedPrefs != null) {
            iValue = mSharedPrefs.getInt(keyName, iDefaultVal);
        } else {
            printDebugStatement("getIntValueForKey() - SharedPrefs not initialized");
        }
        printDebugStatement("getIntValueForKey() - keyName: " + keyName + " , iValue: " + iValue);
        return iValue;
    }

    private void setIntValueForKey(String keyName, Integer iValue) {
        if (mSharedPrefs != null) {
            if (keyName == null || keyName.equals("")) {
                printDebugStatement("setIntValueForKey() - Invalid key");
                return;
            }
            if (iValue >= 0) {
                SharedPreferences.Editor settingEditor = mSharedPrefs.edit();
                settingEditor.putInt(keyName, iValue);
                settingEditor.apply();
            } else {
                printDebugStatement("setIntValueForKey() - Not a valid value: " + iValue);
            }
        } else {
            printDebugStatement("setIntValueForKey() - SharedPrefs not initialized");
        }
    }

    public Boolean getBooleanValueForKey(String keyName, Boolean bFlagDefault) {
        Boolean bFlag = bFlagDefault;
        if (keyName == null || keyName.equals("")) {
            printDebugStatement("getBooleanValueForKey() - Invalid key");
            return false;
        }
        if (mSharedPrefs != null) {
            bFlag = mSharedPrefs.getBoolean(keyName, bFlagDefault);
        } else {
            printDebugStatement("getBooleanValueForKey() - SharedPrefs not initialized");
        }
        printDebugStatement("getBooleanValueForKey() - keyName: " + keyName + " , bFlag: " + bFlag);
        return bFlag;
    }

    public void setBooleanValueForKey(String keyName, Boolean bFlag) {
        if (mSharedPrefs != null) {
            if (keyName == null || keyName.equals("")) {
                printDebugStatement("setBooleanValueForKey() - Invalid key");
                return;
            }
            SharedPreferences.Editor settingEditor = mSharedPrefs.edit();
            settingEditor.putBoolean(keyName, bFlag);
            settingEditor.apply();
        } else {
            printDebugStatement("setBooleanValueForKey() - SharedPrefs not initialized");
        }
    }

    private void setFloatValueForKey(String keyName, float bFlag) {
        if (mSharedPrefs != null) {
            if (keyName == null || keyName.equals("")) {
                printDebugStatement("setFloatValueForKey() - Invalid key");
                return;
            }
            SharedPreferences.Editor settingEditor = mSharedPrefs.edit();
            settingEditor.putFloat(keyName, bFlag);
            settingEditor.apply();
        } else {
            printDebugStatement("setFloatValueForKey() - SharedPrefs not initialized");
        }
    }

    private float getFloatValueForKey(String keyName, float bFlagDefault) {
        float bFlag = bFlagDefault;
        if (keyName == null || keyName.equals("")) {
            printDebugStatement("getFloatValueForKey() - Invalid key");
            return -1;
        }
        if (mSharedPrefs != null) {
            bFlag = mSharedPrefs.getFloat(keyName, bFlagDefault);
        } else {
            printDebugStatement("getFloatValueForKey() - SharedPrefs not initialized");
        }
        printDebugStatement("getFloatValueForKey() - keyName: " + keyName + " , bFlag: " + bFlag);
        return bFlag;
    }


    // accessible methods

    public String getAuthenticationHeader() {
        return getStringValueForKey(PERSISTENCE_KEY_AUTH_HEADER, "");
    }

    public void setAuthenticationHeader(String header) {
        setStringValueForKey(PERSISTENCE_KEY_AUTH_HEADER,  "Bearer "+header);
    }

    public void setDieaseStatus(String DieaseStatus) {
        setStringValueForKey(PERSISTENCE_KEY_DISEASE_STATUS, DieaseStatus);
    }

    public String getDieaseStatus() {
        return getStringValueForKey(PERSISTENCE_KEY_DISEASE_STATUS, "NEGATIVE");
    }

    public void setIsUserLogin(boolean isUserLogin) {
        setBooleanValueForKey(PERSISTENCE_KEY_PRE_IS_LOGIN, isUserLogin);
    }


    public boolean getIsUserLogin() {
        return getBooleanValueForKey(PERSISTENCE_KEY_PRE_IS_LOGIN, false);
    }

    public void setBtAddress(String address) {
        setStringValueForKey(PERSISTENCE_KEY_BT_ADDRESS, address);
    }

    public String getBtAddress() {
        return getStringValueForKey(PERSISTENCE_KEY_BT_ADDRESS, "");
    }

    public void setFcmId(String fcmId) {
        setStringValueForKey(PERSISTENCE_KEY_FCM_ID, fcmId);
    }

    public String getFcmId() {
        return getStringValueForKey(PERSISTENCE_KEY_FCM_ID, "");
    }

    public void setIsSendDetails(boolean isSendDetails) {
        setBooleanValueForKey(PERSISTENCE_KEY_IS_SEND_DETAILS, isSendDetails);
    }

    public boolean getIsSendDetails() {
        return getBooleanValueForKey(PERSISTENCE_KEY_IS_SEND_DETAILS, false);
    }


}
