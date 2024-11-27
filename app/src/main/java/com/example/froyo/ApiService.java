package com.example.froyo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/generate_image")
    Call<ServerResponse> generateImage(@Body GenerateImageRequest request);

}
