package com.example.froyo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // 닉네임 확인
        String nickname = sharedPreferences.getString("user_nickname", null);
        if (nickname == null || nickname.isEmpty()) {
            // 닉네임 설정 화면으로 이동
            Intent intent = new Intent(this, NicknameActivity.class);
            startActivity(intent);
            finish();
        }

        // settings_button 클릭 이벤트
        findViewById(R.id.settings_button).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        // add_button 클릭 이벤트
        findViewById(R.id.add_button).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
            startActivity(intent);
        });

        // folder_button 클릭 이벤트
        findViewById(R.id.folder_button).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FolderActivity.class);
            startActivity(intent);
        });
    }
}
