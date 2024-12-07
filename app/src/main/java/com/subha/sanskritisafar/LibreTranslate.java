package com.subha.sanskritisafar;

import android.util.Log;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibreTranslate {

    public interface TranslationCallback {
        void onTranslationComplete(String translatedText);
    }

    public void translate(String text, String targetLang,TranslationCallback callback) {

        TranslationApi translationApi;

        // Initialize Retrofit and the API interface
        translationApi = ApiClient2.getRetrofitInstance().create(TranslationApi.class);

        // Create a TranslationRequest object with the text and target language
        TranslationRequest request = new TranslationRequest(text, targetLang);

        // Call the API
        Call<TranslationResponse> call = translationApi.translate(request);
        call.enqueue(new Callback<TranslationResponse>() {
            @Override
            public void onResponse(Call<TranslationResponse> call, Response<TranslationResponse> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                    TranslationResponse translationResponse = response.body();
                    String translatedText = translationResponse != null ? translationResponse.getTranslated_text() : "Error";

                    // Use the callback to return the result
                    if (callback != null) {
                        callback.onTranslationComplete(translatedText);
                    }

                } else {
                    Log.e("PRINOUT", "ERROR");
                }
            }

            @Override
            public void onFailure(Call<TranslationResponse> call, Throwable t) {
                // Handle network error
                Log.e("API Error", t.getMessage(), t);
//                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

