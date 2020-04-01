package com.jamdoli.corus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jamdoli.corus.R;
import com.jamdoli.corus.utils.ApiClient;
import com.jamdoli.corus.utils.AppSettingsUsingSharedPrefs;
import com.jamdoli.corus.utils.DebugLogManager;
import com.jamdoli.corus.utils.MyAppContext;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000;
    private static final String LOG_TAG = "SplashScreen:: ";
    AppSettingsUsingSharedPrefs appSettingsUsingSharedPrefs;
    DebugLogManager logManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenSetting();
        setContentView(R.layout.activity_splash_screen);
        MyAppContext.setInstance("SplashScreen", this);
        appSettingsUsingSharedPrefs = AppSettingsUsingSharedPrefs.getInstance();
        logManager = DebugLogManager.getInstance();
        generateToken();
        timer();
    }

    public void screenSetting(){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void timer(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callSettingApi();
            }
        }, SPLASH_TIME_OUT);
    }

    public void generateToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            logManager.logsForError(LOG_TAG, "getInstanceId failed:: " + task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        appSettingsUsingSharedPrefs.setFcmId(token);
                    }
                });
    }

    public void callSettingApi(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("keys", new JsonArray());
        logManager.logsForDebugging(LOG_TAG,jsonObject.toString());
        ApiClient.getPreLoginApiService().appSetting(jsonObject).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                logManager.logsForDebugging(LOG_TAG,response.toString());

                if(response.isSuccessful()){
                    logManager.logsForDebugging(LOG_TAG,response.toString());
                    startActivity(new Intent(SplashScreen.this, IntroActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MyAppContext.getInstance(),"Check your internet connectivity!!",Toast.LENGTH_LONG).show();
            }
        });

    }
}
