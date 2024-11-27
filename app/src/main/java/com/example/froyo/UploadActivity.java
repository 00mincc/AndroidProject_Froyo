package com.example.froyo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {

    private static final String TAG = "UploadActivity";
    private Uri userImageUri; // 선택된 이미지 URI
    private PhotoView userImageView;
    private Button uploadButton;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    userImageUri = result.getData().getData(); // 선택한 이미지 URI 가져오기
                    if (userImageUri != null) {
                        Glide.with(this)
                                .load(userImageUri)
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.error)
                                .into(userImageView); // 선택한 이미지를 PhotoView에 로드
                    } else {
                        Toast.makeText(this, "이미지를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        userImageView = findViewById(R.id.userImageView);
        uploadButton = findViewById(R.id.uploadButton);

        if (userImageView == null) {
            Toast.makeText(this, "PhotoView가 null입니다. XML ID를 확인하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // PhotoView 터치 이벤트 처리
        userImageView.setOnTouchListener(new View.OnTouchListener() {
            private long startTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startTime = System.currentTimeMillis(); // 터치 시작 시간 기록
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    long clickDuration = System.currentTimeMillis() - startTime;
                    if (clickDuration < 200) { // 짧은 터치만 클릭으로 처리
                        openGallery();
                        return true;
                    }
                }
                return false; // PhotoView의 기본 동작을 유지
            }
        });

        // 업로드 버튼 클릭 이벤트
        uploadButton.setOnClickListener(v -> {
            if (userImageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(userImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    sendImageToServer(bitmap, "qwerty61441@gmail.com", "ADMIN");
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

    private void sendImageToServer(Bitmap bitmap, String email, String nickname) {
        String imageBase64 = encodeImageToBase64(bitmap);

        GenerateImageRequest request = new GenerateImageRequest(email, nickname, imageBase64);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        Call<ServerResponse> call = apiService.generateImage(request);

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ServerResponse serverResponse = response.body();

                    // 서버로부터 받은 데이터
                    String srcBase64 = serverResponse.getSrc();
                    String dstBase64 = serverResponse.getDst();
                    String pstCoordinates = serverResponse.getPst();

                    Log.i(TAG, "이미지 생성 성공: src=" + srcBase64 + ", dst=" + dstBase64 + ", pst=" + pstCoordinates);

                    // AppData에 데이터 저장
                    AppData appData = (AppData) getApplication();
                    appData.setSrcBase64(srcBase64);
                    appData.setDstBase64(dstBase64);
                    appData.setPstCoordinates(pstCoordinates);

                    // ComparisonActivity로 이동
                    Intent intent = new Intent(UploadActivity.this, ComparisonActivity.class);
                    startActivity(intent);
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "서버 오류: " + errorBody);
                        Toast.makeText(UploadActivity.this, "서버 오류: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "서버 오류 본문 읽기 실패", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e(TAG, "통신 실패", t);
                Toast.makeText(UploadActivity.this, "통신 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
