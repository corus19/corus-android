package com.jamdoli.corus.utils;


import com.google.gson.JsonObject;
import com.jamdoli.corus.api.model.BulkDeviceRequest;
import com.jamdoli.corus.api.model.BulkDeviceResponse;
import com.jamdoli.corus.api.model.UserInfoRequest;
import com.jamdoli.corus.api.model.UserInfoResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;


public interface ApiInterface {

    @POST("/v1/api/settings")
    Call<JsonObject> appSetting(@Body JsonObject jsonObject);

    @POST("/v1/api/auth/login")
    Call<JsonObject> login(@Body JsonObject jsonObject);

    @POST("/v1/api/user/bulkContactDetails")
    Call<BulkDeviceResponse> bulkDeviceUpload(@Body BulkDeviceRequest bulkDeviceRequest);

    @GET("/v1/api/user")
    Call<UserInfoResponse> getUserInfo();

    @PUT("/v1/api/user")
    Call<JsonObject> putUserinfo(@Body UserInfoRequest userInfoRequest);

    @PUT("/v1/api/user/markpositive")
    Call<JsonObject> markPositive();


}
