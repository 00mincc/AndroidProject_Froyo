package com.example.froyo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private PopupWindow popupWindow;
    private SharedPreferences sharedPreferences;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);

        // Vibrator 초기화
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // settings_button 클릭 이벤트
        findViewById(R.id.settings_button).setOnClickListener(v -> showSettingsPopup());

        // add_button 클릭 이벤트
        findViewById(R.id.add_button).setOnClickListener(v -> {
            // UploadActivity로 이동
            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
            startActivity(intent);
        });

        // folder_button 클릭 이벤트
        findViewById(R.id.folder_button).setOnClickListener(v -> {
            Toast.makeText(this, "폴더 버튼 클릭됨", Toast.LENGTH_SHORT).show();
        });
    }

    private void showSettingsPopup() {
        View settingsView = LayoutInflater.from(this).inflate(R.layout.settings_popup, null);

        popupWindow = new PopupWindow(settingsView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

        // 소리 설정
        Switch soundSwitch = settingsView.findViewById(R.id.sound_switch);
        soundSwitch.setChecked(sharedPreferences.getBoolean("sound_enabled", true));
        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("sound_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "소리 켜짐" : "소리 꺼짐", Toast.LENGTH_SHORT).show();
            if (isChecked) playSound();
        });

        // 진동 설정
        Switch vibrationSwitch = settingsView.findViewById(R.id.vibration_switch);
        vibrationSwitch.setChecked(sharedPreferences.getBoolean("vibration_enabled", true));
        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("vibration_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "진동 켜짐" : "진동 꺼짐", Toast.LENGTH_SHORT).show();
            if (isChecked) triggerVibration();
        });

        // 알림 설정
        Switch notificationSwitch = settingsView.findViewById(R.id.notification_switch);
        notificationSwitch.setChecked(sharedPreferences.getBoolean("notifications_enabled", true));
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "알림 켜짐" : "알림 꺼짐", Toast.LENGTH_SHORT).show();
        });

        // 약관 보기 및 데이터 삭제
        TextView termsTextView = settingsView.findViewById(R.id.terms_text);
        termsTextView.setOnClickListener(v -> Toast.makeText(this, "약관 보기 클릭됨", Toast.LENGTH_SHORT).show());
        settingsView.findViewById(R.id.delete_data_button).setOnClickListener(v -> Toast.makeText(this, "데이터 삭제 버튼 클릭됨", Toast.LENGTH_SHORT).show());

        // 팝업 외부 클릭 시 닫기
        settingsView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });
    }

    private void playSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private void triggerVibration() {
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(500); // 500ms 진동
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
