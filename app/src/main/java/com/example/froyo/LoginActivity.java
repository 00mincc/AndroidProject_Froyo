package com.example.froyo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 100; // Sign-In Request Code
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // activity_login.xml로 설정

        // Google Sign-In 옵션 설정
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // 이메일 요청
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 이미 로그인된 계정 확인
        GoogleSignInAccount existingAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (existingAccount != null) {
            Log.d(TAG, "이미 로그인된 계정 발견: " + existingAccount.getDisplayName());
            navigateToMainActivity(existingAccount);
        }

        // 버튼 설정
        ImageButton startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> signInWithGoogle());
    }

    // Google Sign-In 시작
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // 로그인 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);

                if (account != null) {
                    // 이메일 가져오기
                    String email = account.getEmail();
                    Log.d(TAG, "Google 로그인 성공: " + " (" + email + ")");
                    Toast.makeText(this, "환영합니다!", Toast.LENGTH_SHORT).show();

                    // 이메일 저장
                    SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
                    sharedPreferences.edit().putString("user_email", email).apply();

                    // MainActivity로 이동
                    navigateToMainActivity(account);
                }
            } catch (ApiException e) {
                Log.e(TAG, "Google 인증 실패", e);
                Toast.makeText(this, "Google 인증 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // MainActivity로 이동
    private void navigateToMainActivity(GoogleSignInAccount account) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user_email", account.getEmail());
        intent.putExtra("user_name", account.getDisplayName());
        startActivity(intent);
        finish();
    }

    // 로그아웃 메서드 추가
    public void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    Toast.makeText(LoginActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Google 로그아웃 완료");
                });
    }
}
