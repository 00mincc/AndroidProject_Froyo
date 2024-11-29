package com.example.froyo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FolderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        // 폴더 관리 화면 초기화
        Toast.makeText(this, "폴더 관리 화면입니다.", Toast.LENGTH_SHORT).show();

        // UploadActivity로 이동
        findViewById(R.id.add_button).setOnClickListener(v -> {
            Intent intent = new Intent(FolderActivity.this, UploadActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left); // 왼쪽으로 이동
        });

        // Folder 버튼 클릭: MainActivity로 이동
        findViewById(R.id.folder_button).setOnClickListener(v -> {
            Intent intent = new Intent(FolderActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // 기존 MainActivity 재사용
            startActivity(intent);
        });

        // SettingActivity로 이동
        findViewById(R.id.settings_button).setOnClickListener(v -> {
            Intent intent = new Intent(FolderActivity.this, SettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left); // 왼쪽으로 이동
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right); // 오른쪽으로 이동
    }
}
