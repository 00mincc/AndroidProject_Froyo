package com.example.froyo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://froyo-back-685167908725.asia-east1.run.app";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 타임아웃 설정 추가
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS) // 연결 타임아웃
                    .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)    // 읽기 타임아웃
                    .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)   // 쓰기 타임아웃
                    .build();

            // 커스텀 Gson 설정
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new LenientTypeAdapterFactory()) // 유연한 타입 어댑터 팩토리 등록
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
