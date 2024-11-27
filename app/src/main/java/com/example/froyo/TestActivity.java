package com.example.froyo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";
    private Uri userImageUri;
    private ImageView userImageView;
    private Button generateImageButton;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    userImageUri = result.getData().getData();
                    if (userImageUri != null) {
                        userImageView.setImageURI(userImageUri);
                    } else {
                        Toast.makeText(this, "이미지를 선택하지 못했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        userImageView = findViewById(R.id.userImageView);
        generateImageButton = findViewById(R.id.generateImageButton);

        userImageView.setOnClickListener(v -> openGallery());
        generateImageButton.setOnClickListener(v -> {
            if (userImageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(userImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    generateImage(bitmap, "qwerty61441@gmail.com", "ADMIN");
                } catch (Exception e) {
                    Log.e(TAG, "이미지 처리 실패", e);
                    Toast.makeText(this, "이미지 처리에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "이미지를 먼저 선택해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void generateImage(Bitmap bitmap, String email, String nickname) {
        String imageBase64 = encodeImageToBase64(bitmap);

        GenerateImageRequest request = new GenerateImageRequest(email, nickname, imageBase64);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        Call<ServerResponse> call = apiService.generateImage(request);

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ServerResponse serverResponse = response.body();
                    Log.i(TAG, "이미지 생성 성공: " + serverResponse.getTransformedImage());
                    Toast.makeText(TestActivity.this, "이미지 생성 성공!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "서버 오류: " + errorBody);
                        Toast.makeText(TestActivity.this, "서버 오류: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "서버 오류 본문 읽기 실패", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e(TAG, "통신 실패", t);
                Toast.makeText(TestActivity.this, "통신 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
