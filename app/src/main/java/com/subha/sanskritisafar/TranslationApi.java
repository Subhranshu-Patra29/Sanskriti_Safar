package com.subha.sanskritisafar;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TranslationApi {

    // Define a POST method for the /translate endpoint
    @POST("/translate")
    Call<TranslationResponse> translate(@Body TranslationRequest request);
}



