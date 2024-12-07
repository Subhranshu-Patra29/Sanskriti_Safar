package com.subha.sanskritisafar;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/generate-response")
    Call<ResponseBody> generateResponse(@Body RequestBody requestBody);
}
