package com.example.froyo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ComparisonActivity extends AppCompatActivity {

    private ProgressBar timerProgressBar;
    private static final int TIMER_DURATION = 10000; // 10 seconds
    private static final int TIMER_INTERVAL = 100; // update interval

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);

        // 사용자 업로드 이미지
        ImageView userImageView = findViewById(R.id.userImageView);

        // 서버에서 받아온 이미지 (여기서는 예시 이미지로 설정)
        ImageView serverImageView = findViewById(R.id.serverImageView);
        Glide.with(this)
                .load(R.drawable.placeholder) // 서버에서 받아온 이미지 대신 기본 이미지를 표시
                .placeholder(R.drawable.placeholder) // 로딩 중 표시할 이미지
                .error(R.drawable.error) // 로드 실패 시 표시할 이미지
                .into(serverImageView);

        // UploadActivity에서 전달된 이미지 URI를 가져와서 사용자 업로드 이미지에 표시
        Intent intent = getIntent();
        String userImageUriString = intent.getStringExtra("userImageUri");

        if (userImageUriString != null) {
            Uri userImageUri = Uri.parse(userImageUriString);

            // 선택한 이미지를 상단 ImageView에 표시
            Glide.with(this)
                    .load(userImageUri)
                    .placeholder(R.drawable.placeholder) // 로딩 중 표시할 이미지
                    .error(R.drawable.error) // 로드 실패 시 표시할 이미지
                    .into(userImageView);
        } else {
            Toast.makeText(this, "이미지를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

        // 타이머 ProgressBar 설정
        timerProgressBar = findViewById(R.id.timerProgressBar);
        startTimer();
    }

    private void startTimer() {
        new android.os.CountDownTimer(TIMER_DURATION, TIMER_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
                int progress = (int) (millisUntilFinished / (float) TIMER_DURATION * 100);
                timerProgressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                timerProgressBar.setProgress(0); // 타이머 종료 시 0으로 설정
            }
        }.start();
    }
}
