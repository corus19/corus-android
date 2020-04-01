package com.jamdoli.corus.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.NotificationCompat;
import com.jamdoli.corus.R;
import com.jamdoli.corus.activities.HomeActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AppHelper {

    private static AppHelper instance;
    private String LOG_TAG = "AppHelper()";
    private Activity currentActivity;


    public boolean isTesting = false;

    public boolean uploadVideo = false;

    public boolean isMybiouploaded = false;

    public boolean isFromCameraFrag = false;

    public String recordVideoCameraID = "0";

    public boolean isOTT = false;

    private String deviceInfo;

    public String DEFAULT_COUNTY_CALLING_CODE = "+91";

    private String LOG_FILE_NAME  = "corus_log.txt";

    public String primarymcc = "";

    public String primarymnc = "";

    public String availablememory = "";

    public HashMap<String, String> getCountryNamebycode;
    public HashMap<String, String> getLanguageNamebycode;

    private AppHelper() {
    }

    public static AppHelper getInstance() {
        if (instance == null) {
            instance = new AppHelper();
        }

        return instance;
    }



    public String getAppVersionCode() {
        Context context = MyAppContext.getInstance();
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Not found";
        }
    }

    public String getAppVersionName() {
        Context context = MyAppContext.getInstance();
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }


    public String parseDate(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd/MM/yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.US);
        Date date;
        String str = null;
        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String loadJSONFromAsset(String fileName) {
        String json = "";
        try {
            Context context = MyAppContext.getInstance();
            if (context != null) {
                InputStream is = context.getAssets().open(fileName);

                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                json = new String(buffer, "UTF-8");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
        return json;
    }


    public String getTelepfoniCountryCode(Context mContext) {
        if (mContext != null) {
            TelephonyManager telephonyManager = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE));
            return telephonyManager.getSimCountryIso();
        }
        return "";
    }

    public String getNetworkCountryCode(Context mContext) {
        if (mContext != null) {
            TelephonyManager telephonyManager = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE));
            return telephonyManager.getNetworkCountryIso();
        }
        return "";
    }

    public boolean isInternetOn() {
        boolean val = false;
        Context context = MyAppContext.getInstance();
        if (context != null) {
            ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // ARE WE CONNECTED TO THE NET
            if (connec != null) {
                if ((connec.getNetworkInfo(0) != null && connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)
                        || (connec.getNetworkInfo(0) != null && connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING)
                        || (connec.getNetworkInfo(1) != null && connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING)
                        || (connec.getNetworkInfo(1) != null && connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED)) {
                    val = true;
                } else if ((connec.getNetworkInfo(0) != null && connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED)
                        || (connec.getNetworkInfo(1) != null && connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED)) {
                    val = false;
                }
                return val;
            }
        }
        return false;
    }

    public boolean isWorkingInternetPersent() {
        Context context = MyAppContext.getInstance();
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }

            }
        }
        return false;
    }

    public String verifiedMobileNumber(String mobileNumber) {
        mobileNumber = mobileNumber.replaceAll("[-\\[\\]^/,'*:.!><~@#$%+=?|\"\\\\()\" ]+", "");
        return mobileNumber;
    }


    public String getDeviceId() {
        String deviceid = Settings.System.getString(MyAppContext.getInstance().getContentResolver(), Settings.System.ANDROID_ID);

        DebugLogManager.getInstance().logsForDebugging("AppHelper", "getDeviceId() - " + deviceid);
        return deviceid;
    }

    public String getDeviceName() {
        return Build.MODEL;
    }

    public String getStringLastChar(String string, int lastIndexSize) {
        if (string.length() == lastIndexSize) {
            return string;
        } else if (string.length() > lastIndexSize) {
            return string.substring(string.length() - lastIndexSize);
        } else {
            // whatever is appropriate in this case
            throw new IllegalArgumentException("word has less than " + lastIndexSize + " characters!");
        }
    }

    public boolean isHightBandwidthConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype()));
    }

    private boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return true; // ~ 10+ Mbps
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }


    public void hideKeyboard(Activity activity) {
        DebugLogManager.getInstance().logsForDebugging(activity.getLocalClassName(), "Request for hide keyboard");
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public boolean isFileExist(String fpath) {
        File file = new File(fpath);
        return file.exists();
    }


    public void showAlertDialog(Context context, String alertMessage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(alertMessage);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                context.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    public void showAlertDialogWithFinishActivity(Context context, String alertMessage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(alertMessage);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                context.getString(R.string.ok),
                (dialog, id) -> {
                    ((Activity) context).finish();
                    dialog.cancel();
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    public void showActionAlert(final Context context, String message, String posText,String negText,int actionType) {
        Handler mHandler = new Handler(Looper.getMainLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertDialogBuilder;

                alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogStyle));
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setMessage(message);
                if (actionType != 0) {
                    alertDialogBuilder.setPositiveButton(posText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int arg1) {

                                    // action type 1 for mark positive alert
                                    if(actionType==1){
                                        HomeActivity.getInstance().markPositive();
                                    }
                                    dialog.dismiss();

                                }
                            });
                }
                alertDialogBuilder.setNegativeButton(negText, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorheaderRedDark));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.color_green_text));

            }
        };

        mHandler.post(runnable);
    }






    public static boolean deleteDir(File dir) {
        DebugLogManager.getInstance().logsForDebugging("file",dir.getAbsolutePath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                DebugLogManager.getInstance().logsForDebugging("file",aChildren);
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public String changeIp(String ip) {
        if (!isTesting && ip != null && ip.contains("http://172.20.12.111")) {
            ip = ip.replace("http://172.20.12.111", "https://mm.fyndrapp.com");
        }else{
//            if(ip != null && ip.contains("http://172.20.12.111"))
//                ip = ip.replace("http://172.20.12.111", "http://182.75.17.27:8087");
        }
        return ip;
    }




    public void showNotification(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.build().flags |= Notification.FLAG_INSISTENT;
        notificationManager.notify(notificationId, mBuilder.build());
    }





    private String device_lang;

    public String getDevice_lang() {
        this.device_lang = Locale.getDefault().getLanguage();
        Log.d("AppHelper", ">>" + device_lang);
        return device_lang;
    }

    public String getDeviceInfo() {
        String model = Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        String manufacturer = Build.MANUFACTURER;
        String id = Build.ID;
        String serial = "";
        String availableMemory = "";
       /* int result = ContextCompat.checkSelfPermission(MyAppContext.getInstance(), Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= 26) {
                serial = Build.getSerial();
            }else{
                serial = Build.SERIAL;
            }
        }*/

        String fingerprint = Build.FINGERPRINT;
        model = removeAllSpecialChars(model);
        androidVersion = removeAllSpecialChars(androidVersion);
        manufacturer = removeAllSpecialChars(manufacturer);
        deviceInfo = manufacturer + ":" + androidVersion + ":" + model + ":" + id + ":" + fingerprint;
        Log.e("Device Info", "::::::::" + deviceInfo);
        return deviceInfo;
    }

    private String removeAllSpecialChars(String deviceInfo) {
        Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
        Matcher match = pt.matcher(deviceInfo);
        while (match.find()) {
            String s = match.group();
            deviceInfo = deviceInfo.replaceAll("\\" + s, "");
        }
        return deviceInfo;
    }


    public String getMccMnc() {
        TelephonyManager tel = (TelephonyManager) MyAppContext.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();
        int mcc = 0, mnc = 0;
        if (!TextUtils.isEmpty(networkOperator)) {
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mnc = Integer.parseInt(networkOperator.substring(3));
            primarymcc = networkOperator.substring(0, 3);
            primarymnc = networkOperator.substring(3);
            DebugLogManager.getInstance().logsForDebugging(LOG_TAG, "mnc " + mnc + " mcc " + mcc);
        }
        return mcc + "_" + mnc;
    }

    public String getPrimaryMnc() {
        TelephonyManager tel = (TelephonyManager) MyAppContext.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            primarymcc = networkOperator.substring(0, 3);
            primarymnc = networkOperator.substring(3);
            DebugLogManager.getInstance().logsForDebugging(LOG_TAG, "primary MNC " + primarymnc);
        }
        return primarymnc;
    }

    public String getPrimaryMcc() {
        TelephonyManager tel = (TelephonyManager) MyAppContext.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            primarymcc = networkOperator.substring(0, 3);
            DebugLogManager.getInstance().logsForDebugging(LOG_TAG, "primary MCC " +primarymcc);
        }
        return primarymcc;
    }





    @SuppressLint("MissingPermission")
    public List<String> getSimDetails() {
        List<String> res = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            int mccSlot1 = -1;
            int mccSlot2 = -1;
            int mncSlot1 = -1;
            int mncSlot2 = -1;

            SubscriptionManager subManager = null;
            List<SubscriptionInfo> activeSubscriptionInfoList=null;
        /*    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                subManager = SubscriptionManager.from(MyAppContext.getInstance());
                activeSubscriptionInfoList = subManager.getActiveSubscriptionInfoList();
                if(subManager.getActiveSubscriptionInfoCount() >= 2) {
                    mccSlot2 = subManager.getActiveSubscriptionInfo(1).getMcc();
                    mncSlot2 = subManager.getActiveSubscriptionInfo(1).getMnc();
                    DebugLogManager.getInstance().logsForDebugging(LOG_TAG,"mccSlot2:: "+mccSlot2+" mncSlot2:: "+mncSlot2);

                }
            }*/

            for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                final CharSequence carrierName = subscriptionInfo.getCarrierName();
                final CharSequence displayName = subscriptionInfo.getDisplayName();
                mccSlot1 = subscriptionInfo.getMcc();
                mncSlot1 = subscriptionInfo.getMnc();
                final String subscriptionInfoNumber = subscriptionInfo.getNumber();
                res.add(mccSlot1+":"+mncSlot1);
                DebugLogManager.getInstance().logsForDebugging(LOG_TAG,"mcc:: "+mccSlot1+" mnc:: "+mncSlot1);
            }

        }
        return res;
    }

 /*   public String getPrivatePathDirectory(String directoryName) {
        File cache = MyAppContext.getInstance().getCacheDir();
        Log.e("aaaaaa", cache.getAbsolutePath());
        File appDir = new File(cache.getParent(), directoryName);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        File finalFile = new File(Environment.getExternalStorageDirectory().getPath(), appDir.getPath());
        DebugLogManager.getInstance().logsForDebugging(LOG_TAG, "getPrivatePathDirectory() filePath-: " + finalFile.getAbsolutePath());
        return finalFile.getAbsolutePath();

    }
*/


    public File createLogFile(){
        return new File(MyAppContext.getInstance().getFilesDir(), LOG_FILE_NAME);
    }

    public void saveLogFile(String newlog){
        FileOutputStream outputStream;
        try {
            outputStream = MyAppContext.getInstance().openFileOutput(LOG_FILE_NAME, Context.MODE_APPEND);
            outputStream.write(newlog.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getLogFile() throws IOException {
        FileInputStream fis = MyAppContext.getInstance().openFileInput(LOG_FILE_NAME);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();

    }





    public String convertUTF8ToString(String s) {
        try {
            byte[] tmp2 = Base64.decode(s, Base64.DEFAULT);
            String convertedSting = new String(tmp2, "UTF8");
            DebugLogManager.getInstance().logsForDebugging(LOG_TAG, "convertUTF8ToString -> getting " + s + "  Return " + convertedSting);
            return convertedSting;
        } catch (Exception e) {
            DebugLogManager.getInstance().logsForDebugging(LOG_TAG, "convertUTF8ToString -> not able to convert " + s);

            return s;
        }
    }



    private boolean isBase64(String stringBase64){
        String regex =
                "([A-Za-z0-9+/]{4})*"+
                        "([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)";

        Pattern patron = Pattern.compile(regex);

        if (!patron.matcher(stringBase64).matches()) {
            return false;
        } else {
            return true;
        }
    }


    public String convertStringToUTF8(String s){
        byte[] data;

        try {
            data = s.getBytes("UTF-8");
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }





    public String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        availablememory = formatSize(availableBlocks * blockSize);
        return availablememory;
    }


    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }



    public static byte[] getBytesFromUUID(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }



    public static UUID getUUIDFromBytes(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        Long high = byteBuffer.getLong();
        Long low = byteBuffer.getLong();

        return new UUID(high, low);
    }

}
