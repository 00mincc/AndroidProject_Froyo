package com.example.froyo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class ComparisonActivity extends AppCompatActivity {

    private static final int TIMER_DURATION = 60000; // 60 seconds
    private static final int TIMER_INTERVAL = 1000; // 1 second
    private View[] timerBlocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);

        // 사용자 업로드 이미지
        ImageView userImageView = findViewById(R.id.userImageView);

        // 서버에서 받아온 이미지 (여기서는 예시 이미지로 설정)
        ImageView serverImageView = findViewById(R.id.serverImageView);
        Glide.with(this)
                .load(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(serverImageView);

        Intent intent = getIntent();
        String userImageUriString = intent.getStringExtra("userImageUri");

        if (userImageUriString != null) {
            Uri userImageUri = Uri.parse(userImageUriString);
            Glide.with(this)
                    .load(userImageUri)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(userImageView);
        } else {
            Toast.makeText(this, "이미지를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

        // 블록을 배열에 담기
        timerBlocks = new View[]{
                findViewById(R.id.block1), findViewById(R.id.block2),
                findViewById(R.id.block3), findViewById(R.id.block4),
                findViewById(R.id.block5), findViewById(R.id.block6),
                findViewById(R.id.block7), findViewById(R.id.block8),
                findViewById(R.id.block9), findViewById(R.id.block10)
        };

        // 타이머 시작
        startTimer();
    }

    private void startTimer() {
        new CountDownTimer(TIMER_DURATION, TIMER_INTERVAL) {
            int currentBlock = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished <= TIMER_DURATION - (currentBlock + 1) * 10000L) {
                    if (currentBlock < timerBlocks.length) {
                        timerBlocks[currentBlock].setBackgroundColor(0xFFFF0000); // 빨간색으로 변경
                        currentBlock++;
                    }
                }
            }

            @Override
            public void onFinish() {
                // 타이머 종료 시 LoginActivity로 돌아가기
                Intent intent = new Intent(ComparisonActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        }.start();
    }
}
