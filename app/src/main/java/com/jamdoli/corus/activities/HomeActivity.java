package com.jamdoli.corus.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.gson.JsonObject;
import com.jamdoli.corus.R;
import com.jamdoli.corus.api.model.UserInfoRequest;
import com.jamdoli.corus.api.model.UserInfoResponse;
import com.jamdoli.corus.service.ForegroundService;
import com.jamdoli.corus.utils.ApiClient;
import com.jamdoli.corus.utils.AppHelper;
import com.jamdoli.corus.utils.AppSettingsUsingSharedPrefs;
import com.jamdoli.corus.utils.DebugLogManager;
import com.jamdoli.corus.utils.MyAppContext;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private static final String LOG_TAG = "HomeActivity:: ";
    Button btMarkPositive;
    AppSettingsUsingSharedPrefs appSettingsUsingSharedPrefs;
    DebugLogManager logManager;
    private static HomeActivity instance;
    LinearLayout markLinLayout, thanksLinLayout;


    private void setInstance(HomeActivity instance) {
        HomeActivity.instance = instance;
    }

    public static HomeActivity getInstance() {
        if (instance == null) {
            instance = new HomeActivity();
        }
        return HomeActivity.instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        MyAppContext.setInstance("HomeActivity",this);
        setInstance(this);
        logManager = DebugLogManager.getInstance();
        appSettingsUsingSharedPrefs = AppSettingsUsingSharedPrefs.getInstance();
        uiInitialize();
        btMarkPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // action type 1 for mark positive alert
                AppHelper.getInstance().showActionAlert(HomeActivity.this,getString(R.string.mark_postitve_dialog_text),"OK","Cancel",1);
            }
        });
        startForegroundService();
    }

    public void uiInitialize(){
        btMarkPositive =  findViewById(R.id.home_bt_positive);
        markLinLayout = findViewById(R.id.home_frag_markContainer);
        thanksLinLayout = findViewById(R.id.home_frag_thanksContainer);
    }
    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
        putUserInfo();
    }

    public void getUserInfo(){

        ApiClient.getApiService().getUserInfo().enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                logManager.logsForDebugging(LOG_TAG,"getUserInfo response:: "+response.toString());
                if(response.isSuccessful()){
                    logManager.logsForDebugging(LOG_TAG,response.toString());
                    appSettingsUsingSharedPrefs.setDieaseStatus(response.body().getCovidContactStatus());
                    appSettingsUsingSharedPrefs.setIsSendDetails(Boolean.parseBoolean(response.body().getSendContactDetails()));
                    appSettingsUsingSharedPrefs.setBtAddress(response.body().getBluetoothSignature());

                    if(response.body().getCovidContactStatus().equalsIgnoreCase("positive")){
                        btMarkPositive.setClickable(false);
                        btMarkPositive.setText(R.string.marked_positive);
                        btMarkPositive.setAlpha(0.4F);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {

            }
        });
    }


    public void putUserInfo(){

        UserInfoRequest userInfoRequest = UserInfoRequest.builder()
                    .bluetoothSignature(appSettingsUsingSharedPrefs.getBtAddress())
                    .deviceId(AppHelper.getInstance().getDeviceId())
                    .fcmId(appSettingsUsingSharedPrefs.getFcmId())
                    .os("ANDROID")
                    .osVersion(Build.VERSION.SDK_INT+"")
                    .build();

        logManager.logsForDebugging(LOG_TAG,"putUserInfo request:: "+userInfoRequest.toString());
        ApiClient.getApiService().putUserinfo(userInfoRequest).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                logManager.logsForDebugging(LOG_TAG,"putUserInfo response:: "+response.toString());
                if(response.isSuccessful()){
                    logManager.logsForDebugging(LOG_TAG,response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    public void markPositive(){

        ApiClient.getApiService().markPositive().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                logManager.logsForDebugging(LOG_TAG,response.toString());
                if(response.isSuccessful()){
                    logManager.logsForDebugging(LOG_TAG,response.body().toString());
                    markLinLayout.setVisibility(View.GONE);
                    thanksLinLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    public void startForegroundService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "We have got you covered");
        ContextCompat.startForegroundService(this, serviceIntent);
    }
}
