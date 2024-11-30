package com.example.froyo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class AccountManagementActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView accountEmailText;
    private EditText nicknameEditText;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // Google Sign-In 클라이언트 초기화
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 현재 계정 이메일 표시
        accountEmailText = findViewById(R.id.account_email_text);
        String userEmail = sharedPreferences.getString("user_email", "이메일 정보 없음");
        accountEmailText.setText("현재 계정: " + userEmail);

        // 닉네임 변경 기능
        nicknameEditText = findViewById(R.id.nickname_edit_text);
        Button changeNicknameButton = findViewById(R.id.change_nickname_button);
        changeNicknameButton.setOnClickListener(v -> {
            String newNickname = nicknameEditText.getText().toString().trim();
            if (newNickname.isEmpty()) {
                Toast.makeText(this, "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
            } else if (newNickname.length() > 20) {
                Toast.makeText(this, "닉네임은 20자 이내로 설정하세요.", Toast.LENGTH_SHORT).show();
            } else {
                sharedPreferences.edit().putString("user_nickname", newNickname).apply();
                Toast.makeText(this, "닉네임이 '" + newNickname + "'(으)로 변경되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 계정 변경 기능
        Button changeAccountButton = findViewById(R.id.change_account_button);
        changeAccountButton.setOnClickListener(v -> {
            // 닉네임 데이터만 삭제
            sharedPreferences.edit().remove("user_nickname").apply();

            // Google 로그아웃 처리
            mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
                Toast.makeText(this, "계정이 초기화되었습니다. 새로운 계정을 등록하세요.", Toast.LENGTH_SHORT).show();

                // LoginActivity로 이동
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });
    }
}
