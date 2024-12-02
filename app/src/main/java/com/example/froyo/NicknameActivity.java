package com.example.froyo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NicknameActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname); // activity_nickname.xml로 설정

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        EditText nicknameInput = findViewById(R.id.nickname_input);
        ImageButton saveButton = findViewById(R.id.save_button);

        // 저장 버튼 클릭 이벤트
        saveButton.setOnClickListener(v -> {
            String nickname = nicknameInput.getText().toString().trim();
            if (!nickname.isEmpty()) {
                // 닉네임 저장
                sharedPreferences.edit().putString("user_nickname", nickname).apply();
                Toast.makeText(this, "닉네임이 저장되었습니다.", Toast.LENGTH_SHORT).show();

                // MainActivity로 이동
                Intent intent = new Intent(NicknameActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
