package com.example.froyo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class SettingActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Google Sign-In 클라이언트 초기화
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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

        // 계정 관리 버튼 설정
        findViewById(R.id.management_button).setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, AccountManagementActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // 로그아웃 버튼 설정
        findViewById(R.id.logoout_button).setOnClickListener(v -> {
            // Google 로그아웃
            mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
                // SharedPreferences에서 사용자 정보 제거
                sharedPreferences.edit().remove("user_email").apply();
                Toast.makeText(this, "로그아웃 완료", Toast.LENGTH_SHORT).show();

                // LoginActivity로 이동
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        });

        // 하단 네비게이션 바의 버튼 동작 복구
        findViewById(R.id.add_button).setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        });

        findViewById(R.id.folder_button).setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, FolderActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // add_button 화면 전환 시 오른쪽 이동 애니메이션 추가
        findViewById(R.id.add_button).setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, UploadActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        });

        // settings_button 클릭 시 MainActivity로 이동
        findViewById(R.id.settings_button).setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right); // 오른쪽으로 이동
    }
}
