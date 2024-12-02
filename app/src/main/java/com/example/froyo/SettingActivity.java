package com.example.froyo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
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
            showAccountManagementPopup();
        });

        findViewById(R.id.folder_button).setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, FolderActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
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

    private void showAccountManagementPopup() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_account_management, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        View dimView = findViewById(R.id.dim_view);
        dimView.setVisibility(View.VISIBLE);

        // 닉네임 변경 버튼
        popupView.findViewById(R.id.nickname_change_button).setOnClickListener(v -> {
            showNicknameChangePopup();
        });

        // 로그아웃 버튼
        popupView.findViewById(R.id.logout_button).setOnClickListener(v -> {
            showLogoutConfirmPopup();
        });

        // 계정 탈퇴 버튼
        popupView.findViewById(R.id.account_delete_button).setOnClickListener(v -> {
            showDeleteConfirmPopup();
        });

        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

        popupWindow.setOnDismissListener(() -> {
            dimView.setVisibility(View.GONE);
        });
    }

    private void showNicknameChangePopup() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_nickname_change, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText nicknameEdit = popupView.findViewById(R.id.nickname_edit);
        popupView.findViewById(R.id.confirm_nickname).setOnClickListener(v -> {
            String newNickname = nicknameEdit.getText().toString();
            if (!newNickname.isEmpty()) {
                // 닉네임 변경 로직 (SharedPreferences에 저장)
                SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
                sharedPreferences.edit().putString("user_nickname", newNickname).apply();
                Toast toast = Toast.makeText(this, "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 200); // 위치 위로 조금 올림
                toast.show();
                popupWindow.dismiss();

                // 채팅창 사라지게 설정 (팝업 종료)
                View dimView = findViewById(R.id.dim_view);
                dimView.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 취소 버튼 클릭 이벤트
        popupView.findViewById(R.id.cancel_nickname).setOnClickListener(v -> {
            popupWindow.dismiss();
        });

        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }

    private void showLogoutConfirmPopup() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_logout_confirm, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        // 로그아웃 확인 버튼 클릭 이벤트
        popupView.findViewById(R.id.confirm_logout).setOnClickListener(v -> {
            // Google 로그아웃
            mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
                // SharedPreferences에서 사용자 정보 제거
                SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
                sharedPreferences.edit().remove("user_email").apply();
                Toast.makeText(this, "로그아웃 완료", Toast.LENGTH_SHORT).show();

                // LoginActivity로 이동
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
            popupWindow.dismiss();
        });

        // 취소 버튼 클릭 이벤트
        popupView.findViewById(R.id.cancel_logout).setOnClickListener(v -> {
            popupWindow.dismiss();
        });

        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }

    private void showDeleteConfirmPopup() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_delete_confirm, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        // 계정 삭제 확인 버튼 클릭 이벤트
        popupView.findViewById(R.id.confirm_delete).setOnClickListener(v -> {
            // 계정 삭제 로직 (로그아웃 로직 포함)
            mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
                // SharedPreferences에서 사용자 정보 제거
                SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                Toast.makeText(this, "계정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                // LoginActivity로 이동
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
            popupWindow.dismiss();
        });

        // 취소 버튼 클릭 이벤트
        popupView.findViewById(R.id.cancel_delete).setOnClickListener(v -> {
            popupWindow.dismiss();
        });

        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }
}
