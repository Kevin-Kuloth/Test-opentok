package com.tokbox.android.demo.learningopentok;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Kevin on 12/1/2016.
 */

public interface MyApiEndpointInterface {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter


    @POST("call")
    Call<User> call(@Body UserCallrequest userCallrequest);

    @POST("user")
    Call<Usercreated> usercreate(@Body Usercreate usercreate);

    @GET("user")
    Call<Usercreated> getuser();
}