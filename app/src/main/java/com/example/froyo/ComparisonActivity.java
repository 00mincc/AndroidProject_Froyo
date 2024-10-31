package com.example.froyo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ComparisonActivity extends AppCompatActivity {

    private ImageView userImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);

        userImageView = findViewById(R.id.userImageView);

        // UploadActivity에서 전달된 이미지 URI를 가져와서 표시
        Intent intent = getIntent();
        String userImageUriString = intent.getStringExtra("userImageUri");

        if (userImageUriString != null) {
            Uri userImageUri = Uri.parse(userImageUriString);

            // 선택한 이미지를 왼쪽 ImageView에 표시
            Glide.with(this)
                    .load(userImageUri)
                    .placeholder(R.drawable.placeholder) // 로딩 중 표시할 이미지
                    .error(R.drawable.error) // 로드 실패 시 표시할 이미지
                    .into(userImageView);
        } else {
            Toast.makeText(this, "이미지를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
