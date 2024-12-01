package com.example.froyo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {

    private static final String TAG = "UploadActivity";
    private Uri userImageUri;
    private ImageView userImageView;
    private ImageButton uploadButton;
    private FrameLayout loadingOverlay;
    private Matrix matrix;
    private ScaleGestureDetector scaleGestureDetector;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    userImageUri = result.getData().getData();
                    if (userImageUri != null) {
                        Glide.with(this)
                                .load(userImageUri)
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.error)
                                .into(userImageView);
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
        ImageButton deleteButton = findViewById(R.id.deleteButton);
        loadingOverlay = findViewById(R.id.loading_overlay);

        matrix = new Matrix();
        userImageView.setImageMatrix(matrix);

        // 확대/축소 제스처를 처리할 ScaleGestureDetector 설정
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
                userImageView.setImageMatrix(matrix);
                return true;
            }
        });

        userImageView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            return true;
        });

        setupNavigationBar();

        // 삭제 버튼 클릭 시 갤러리 열기
        deleteButton.setOnClickListener(v -> openGallery());

        // 업로드 버튼 클릭 이벤트
        uploadButton.setOnClickListener(v -> {
            if (userImageUri != null) {
                try {
                    // 로딩 오버레이 활성화
                    showLoadingOverlay();

                    InputStream inputStream = getContentResolver().openInputStream(userImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    // 캡처된 이미지 처리
                    Bitmap capturedBitmap = captureZoomedImage(bitmap);

                    sendImageToServer(capturedBitmap, "qwerty61441@gmail.com", "ADMIN", () -> {
                        // 로딩 오버레이 비활성화
                        hideLoadingOverlay();
                    });

                } catch (Exception e) {
                    Log.e(TAG, "이미지 처리 실패", e);
                    Toast.makeText(this, "이미지 처리에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    hideLoadingOverlay();
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

    private void setupNavigationBar() {
        // Folder 버튼 클릭
        findViewById(R.id.folder_button).setOnClickListener(v -> {
            if (isLoadingOverlayVisible()) return; // 로딩 중에는 비활성화
            Intent intent = new Intent(UploadActivity.this, FolderActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        });

        // Add 버튼 클릭
        findViewById(R.id.add_button).setOnClickListener(v -> {
            if (isLoadingOverlayVisible()) return; // 로딩 중에는 비활성화
            Intent intent = new Intent(UploadActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        // Settings 버튼 클릭
        findViewById(R.id.settings_button).setOnClickListener(v -> {
            if (isLoadingOverlayVisible()) return; // 로딩 중에는 비활성화
            Intent intent = new Intent(UploadActivity.this, SettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        });
    }

    private void showLoadingOverlay() {
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingOverlay.bringToFront(); // 로딩 오버레이를 최상단으로 설정
    }

    private void hideLoadingOverlay() {
        loadingOverlay.setVisibility(View.GONE);
    }

    private boolean isLoadingOverlayVisible() {
        return loadingOverlay.getVisibility() == View.VISIBLE;
    }

    private void sendImageToServer(Bitmap bitmap, String email, String nickname, Runnable onComplete) {
        String imageBase64 = encodeImageToBase64(bitmap);

        GenerateImageRequest request = new GenerateImageRequest(email, nickname, imageBase64);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        Call<ServerResponse> call = apiService.generateImage(request);

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ServerResponse serverResponse = response.body();

                    String srcBase64 = serverResponse.getSrc();
                    String dstBase64 = serverResponse.getDst();
                    List<List<Integer>> pts = serverResponse.getPts();

                    Log.i(TAG, "이미지 생성 성공: src=" + srcBase64 + ", dst=" + dstBase64 + ", pts=" + pts);

                    if (getApplication() instanceof AppData) {
                        AppData appData = (AppData) getApplication();
                        appData.setSrcBase64(srcBase64);
                        appData.setDstBase64(dstBase64);
                        appData.setPtsCoordinates(pts);
                    } else {
                        Log.e(TAG, "getApplication()이 AppData의 인스턴스가 아닙니다.");
                        Toast.makeText(UploadActivity.this, "데이터 저장 실패", Toast.LENGTH_SHORT).show();
                    }

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
                onComplete.run();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e(TAG, "통신 실패", t);
                Toast.makeText(UploadActivity.this, "통신 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                onComplete.run();
            }
        });
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private Bitmap captureZoomedImage(Bitmap originalBitmap) {
        // 이미지의 확대/축소 상태를 반영한 비트맵 캡처
        Bitmap bitmap = Bitmap.createBitmap(originalBitmap);
        userImageView.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(userImageView.getDrawingCache());
        userImageView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    @Override
    public void onBackPressed() {
        if (isLoadingOverlayVisible()) return; // 로딩 중에는 뒤로가기 비활성화
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}