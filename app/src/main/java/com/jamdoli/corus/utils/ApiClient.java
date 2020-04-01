package com.jamdoli.corus.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://corus19-1.eu-west-1.elasticbeanstalk.com";//AppSettingsUsingSharedPrefs.getInstance().getApiBaseUrl();
    private static Retrofit retrofit = null;
    private static Retrofit mResourceRetrofit = null;

    private static String authHeader = "";
    private static String lang = "en";
    public static Retrofit getClient() {
        if (!authHeader.equalsIgnoreCase(AppSettingsUsingSharedPrefs.getInstance().getAuthenticationHeader())) {
            retrofit = null;
        }
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

       if (retrofit == null) {
           authHeader = AppSettingsUsingSharedPrefs.getInstance().getAuthenticationHeader();



                OkHttpClient.Builder unsafeOkHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient().newBuilder().addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Content-type", "application/json")
                            .header("Authorization", authHeader)
                            .header("DeviceInfo",AppHelper.getInstance().getDeviceInfo())
                            .header("DeviceId",AppHelper.getInstance().getDeviceId())
                            .header("DeviceType","ANDROID")
                            .build();
                    DebugLogManager.getInstance().logsForDebugging("headers", ":" + request.headers());
                    return chain.proceed(request);
                });


           /// OkHttpClient client = httpClient.build().;
            OkHttpClient client = unsafeOkHttpClient
                    .connectTimeout(45, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit;
    }


    public static Retrofit preLoginCient() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (retrofit == null) {
            OkHttpClient.Builder unsafeOkHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient().newBuilder().addInterceptor(chain -> {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Content-type", "application/json")
                        .header("DeviceInfo",AppHelper.getInstance().getDeviceInfo())
                        .header("DeviceId",AppHelper.getInstance().getDeviceId())
                        .header("DeviceType","ANDROID")
                        .build();
                DebugLogManager.getInstance().logsForDebugging("headers", ":" + request.headers());
                return chain.proceed(request);
            });


            /// OkHttpClient client = httpClient.build().;
            OkHttpClient client = unsafeOkHttpClient
                    .connectTimeout(45, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit;
    }



    public static ApiInterface getPreLoginApiService() {
        return preLoginCient().create(ApiInterface.class);
    }

    public static ApiInterface getApiService() {
        return getClient().create(ApiInterface.class);
    }
}
