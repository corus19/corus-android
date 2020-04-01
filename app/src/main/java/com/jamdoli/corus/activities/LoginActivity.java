package com.jamdoli.corus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.JsonObject;
import com.jamdoli.corus.R;
import com.jamdoli.corus.api.model.UserInfoResponse;
import com.jamdoli.corus.utils.ApiClient;
import com.jamdoli.corus.utils.AppHelper;
import com.jamdoli.corus.utils.AppSettingsUsingSharedPrefs;
import com.jamdoli.corus.utils.DebugLogManager;
import com.jamdoli.corus.utils.MyAppContext;
import com.jamdoli.corus.utils.ProgressDialogCustom;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = "LoginActivity:: ";
    private LoginButton uiFb_loginButton;
    private Button fbLogin;
    private DebugLogManager logManager;
    ProgressDialogCustom progressDialogCustom;
    CallbackManager callbackManager;
    private Bundle bundle = new Bundle();
    AppSettingsUsingSharedPrefs appSettingsUsingSharedPrefs;
    AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        uiInitialize();
        callbackManager = CallbackManager.Factory.create();
        appSettingsUsingSharedPrefs = AppSettingsUsingSharedPrefs.getInstance();
        fbLogin.setOnClickListener(view -> {
            accessToken = AccessToken.getCurrentAccessToken();
            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
            if(!isLoggedIn){
                initializeFacebookSdk();
            }
            else{
                Intent homeIntent = new Intent(LoginActivity.this,HomeActivity.class);
                callLoginApi(homeIntent,accessToken.getToken());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialogCustom.dismiss();
    }

    public void uiInitialize(){
        logManager = DebugLogManager.getInstance();
        progressDialogCustom = new ProgressDialogCustom(this);
        fbLogin =  findViewById(R.id.activity_login_loginFb);
        uiFb_loginButton= findViewById(R.id.activity_login_btFb);
    }


    private void initializeFacebookSdk() {
        logManager.logsForDebugging(LOG_TAG, "Facebook Login click ");
        uiFb_loginButton.setPermissions("public_profile", "email");
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY);
        uiFb_loginButton.performClick();
        progressDialogCustom.show();
        uiFb_loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                logManager.logsForDebugging(LOG_TAG, object.toString() + " :: response :: " + response.getJSONArray());
                                Toast.makeText(LoginActivity.this,object.toString(),Toast.LENGTH_LONG).show();
                                // Application code
                                fbLogin.setText(R.string.logout);
                                try {

                                    String userName = object.getString("name");
                                    String email = object.getString("email");
                                    String fbKey = object.getString("id");
                                    String imageUrl = "https://graph.facebook.com/" + fbKey + "/picture?type=large";

                                    bundle.putString("name", userName);
                                    bundle.putString("imageUrl", imageUrl);
                                    bundle.putString("email",email);
                                    Intent homeIntent = new Intent(LoginActivity.this,HomeActivity.class);
                                    homeIntent.putExtras(bundle);
                                    callLoginApi(homeIntent,accessToken.getToken());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                progressDialogCustom.dismiss();
                logManager.logsForDebugging(LOG_TAG, "Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                progressDialogCustom.dismiss();
                AppHelper.getInstance().showAlertDialog(LoginActivity.this, getString(R.string.something_went_wrong));
                logManager.logsForDebugging(LOG_TAG, error.toString());
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
    }


    public void callLoginApi(Intent intent ,String accessToken){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fbToken", accessToken.toString());
       // jsonObject.addProperty("bluetoothSignature", appSettingsUsingSharedPrefs.getBtAddress());

        logManager.logsForDebugging(LOG_TAG,jsonObject.toString());
        ApiClient.getPreLoginApiService().login(jsonObject).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                logManager.logsForDebugging(LOG_TAG,response.toString());
                if (response.isSuccessful()&& response.body()!=null) {
                    logManager.logsForDebugging(LOG_TAG,response.body().toString());
                    appSettingsUsingSharedPrefs.setIsUserLogin(true);
                    appSettingsUsingSharedPrefs.setAuthenticationHeader(response.body().get("authToken").getAsString());
                    getUserInfo(intent);
                }else              progressDialogCustom.dismiss();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MyAppContext.getInstance(),"Login Failed!! Try again",Toast.LENGTH_LONG).show();
                progressDialogCustom.dismiss();
            }
        });
    }

    public void getUserInfo(Intent intent){

        ApiClient.getApiService().getUserInfo().enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                logManager.logsForDebugging(LOG_TAG,"getUserInfo response:: "+response.toString());
                progressDialogCustom.dismiss();
                if(response.isSuccessful()){
                    logManager.logsForDebugging(LOG_TAG,response.toString());
                    appSettingsUsingSharedPrefs.setDieaseStatus(response.body().getCovidContactStatus());
                    appSettingsUsingSharedPrefs.setIsSendDetails(Boolean.parseBoolean(response.body().getSendContactDetails()));
                    appSettingsUsingSharedPrefs.setBtAddress(response.body().getBluetoothSignature());
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                progressDialogCustom.dismiss();
                Toast.makeText(MyAppContext.getInstance(),"Login Failed!! Try again",Toast.LENGTH_LONG).show();
            }
        });
    }

}
