package com.paresh.gofynd.network;

import com.paresh.gofynd.model.MainModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NetworkInterface {

    @GET("everything")
    Call<MainModel> getData(@Query("apiKey") String api_key,
                            @Query("q") String site,
                            @Query("language") String language,
                            @Query("pageSize") int pageSize,
                            @Query("page") int page);
}
