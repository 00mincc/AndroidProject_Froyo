package com.example.froyo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // 버튼을 눌렀을 때 Toast 메시지 출력
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Toast.makeText(SecondActivity.this, "이전 페이지로 이동", Toast.LENGTH_SHORT).show();
            finish(); // 이전 페이지로 돌아감
        });
    }
}