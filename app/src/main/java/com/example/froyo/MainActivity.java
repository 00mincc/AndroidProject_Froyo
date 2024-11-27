package com.example.froyo;

import android.content.Context;
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

        // 기존 SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);

        // MediaPlayer 초기화 (소리 효과용)
        //mediaPlayer = MediaPlayer.create(this, R.raw.sample_sound); // sample_sound는 raw 폴더에 추가된 파일

        // Vibrator 초기화
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // 기존 버튼 동작 유지
        findViewById(R.id.settings_button).setOnClickListener(v -> showSettingsPopup());
        findViewById(R.id.add_button).setOnClickListener(v -> {
            // 기존 + 버튼 동작 (새로운 액티비티 실행 등)
            Toast.makeText(this, "+ 버튼 클릭됨", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.folder_button).setOnClickListener(v -> {
            // 기존 폴더 버튼 동작
            Toast.makeText(this, "폴더 버튼 클릭됨", Toast.LENGTH_SHORT).show();
        });
    }

    // 기존 기능 유지: 팝업 UI 표시
    private void showSettingsPopup() {
        View settingsView = LayoutInflater.from(this).inflate(R.layout.settings_popup, null);

        popupWindow = new PopupWindow(settingsView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

        // 기존 소리 설정
        Switch soundSwitch = settingsView.findViewById(R.id.sound_switch);
        soundSwitch.setChecked(sharedPreferences.getBoolean("sound_enabled", true));
        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("sound_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "소리 켜짐" : "소리 꺼짐", Toast.LENGTH_SHORT).show();
            if (isChecked) playSound();
        });

        // 기존 진동 설정
        Switch vibrationSwitch = settingsView.findViewById(R.id.vibration_switch);
        vibrationSwitch.setChecked(sharedPreferences.getBoolean("vibration_enabled", true));
        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("vibration_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "진동 켜짐" : "진동 꺼짐", Toast.LENGTH_SHORT).show();
            if (isChecked) triggerVibration();
        });

        // 기존 알림 설정
        Switch notificationSwitch = settingsView.findViewById(R.id.notification_switch);
        notificationSwitch.setChecked(sharedPreferences.getBoolean("notifications_enabled", true));
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "알림 켜짐" : "알림 꺼짐", Toast.LENGTH_SHORT).show();
        });

        // 기존 약관 및 데이터 삭제 버튼 유지
        TextView termsTextView = settingsView.findViewById(R.id.terms_text);
        termsTextView.setOnClickListener(v -> {
            Toast.makeText(this, "약관 보기 클릭됨", Toast.LENGTH_SHORT).show();
        });
        settingsView.findViewById(R.id.delete_data_button).setOnClickListener(v -> {
            Toast.makeText(this, "데이터 삭제 버튼 클릭됨", Toast.LENGTH_SHORT).show();
        });

        // dim 영역 닫기 설정 유지
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
