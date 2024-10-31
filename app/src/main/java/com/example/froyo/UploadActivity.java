package com.example.froyo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class UploadActivity extends AppCompatActivity {

    private Uri userImageUri; // 선택된 이미지 URI를 저장하는 변수
    private ImageView userImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        userImageView = findViewById(R.id.userImageView);
        Button uploadButton = findViewById(R.id.uploadButton);

        // MainActivity에서 전달된 이미지 URI 가져오기
        Intent intent = getIntent();
        String imageUriString = intent.getStringExtra("userImageUri");

        if (imageUriString != null) {
            userImageUri = Uri.parse(imageUriString);

            // 선택한 이미지를 ImageView에 표시
            Glide.with(this)
                    .load(userImageUri)
                    .placeholder(R.drawable.placeholder) // 로딩 중 표시할 이미지
                    .error(R.drawable.error) // 로드 실패 시 표시할 이미지
                    .into(userImageView);
        } else {
            Toast.makeText(this, "이미지를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

        // "이미지 업로드" 버튼 클릭 시 ComparisonActivity로 이동
        uploadButton.setOnClickListener(v -> {
            if (userImageUri != null) {
                Intent comparisonIntent = new Intent(UploadActivity.this, ComparisonActivity.class);
                comparisonIntent.putExtra("userImageUri", userImageUri.toString()); // 이미지 URI 전달
                startActivity(comparisonIntent); // ComparisonActivity로 이동
            } else {
                Toast.makeText(this, "이미지를 선택해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
