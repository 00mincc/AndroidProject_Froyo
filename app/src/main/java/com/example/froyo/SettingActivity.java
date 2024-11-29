package com.example.froyo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // SharedPreferences 초기화
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // 소리 설정
        Switch soundSwitch = findViewById(R.id.sound_switch);
        soundSwitch.setChecked(sharedPreferences.getBoolean("sound_enabled", true));
        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("sound_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "소리 켜짐" : "소리 꺼짐", Toast.LENGTH_SHORT).show();
        });

        // 진동 설정
        Switch vibrationSwitch = findViewById(R.id.vibration_switch);
        vibrationSwitch.setChecked(sharedPreferences.getBoolean("vibration_enabled", true));
        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("vibration_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "진동 켜짐" : "진동 꺼짐", Toast.LENGTH_SHORT).show();
        });

        // 알림 설정
        Switch notificationSwitch = findViewById(R.id.notification_switch);
        notificationSwitch.setChecked(sharedPreferences.getBoolean("notifications_enabled", true));
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "알림 켜짐" : "알림 꺼짐", Toast.LENGTH_SHORT).show();
        });

        // 약관 보기
        TextView termsText = findViewById(R.id.terms_text);
        termsText.setOnClickListener(v -> Toast.makeText(this, "약관 보기 클릭됨", Toast.LENGTH_SHORT).show());

        // 데이터 삭제
        findViewById(R.id.delete_data_button).setOnClickListener(v -> {
            Toast.makeText(this, "데이터 삭제 버튼 클릭됨", Toast.LENGTH_SHORT).show();
        });

        // 폴더 버튼 클릭 이벤트
        findViewById(R.id.folder_button).setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, FolderActivity.class);
            startActivity(intent);
        });

        // + 버튼 클릭 이벤트
        findViewById(R.id.add_button).setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, UploadActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right); // 오른쪽으로 이동
        });

        // 설정 버튼 클릭 이벤트
        findViewById(R.id.settings_button).setOnClickListener(v -> {
            Toast.makeText(this, "현재 설정 화면입니다.", Toast.LENGTH_SHORT).show();
        });

        // UploadActivity로 이동
        findViewById(R.id.add_button).setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, UploadActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right); // 오른쪽으로 이동
        });

        // Folder 버튼 클릭: MainActivity로 이동
        findViewById(R.id.settings_button).setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // 기존 MainActivity 재사용
            startActivity(intent);
        });

        // FolderActivity로 이동
        findViewById(R.id.folder_button).setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, FolderActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right); // 오른쪽으로 이동
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right); // 오른쪽으로 이동
    }
}
