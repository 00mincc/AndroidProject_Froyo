package com.example.froyo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // settings_button 클릭 이벤트
        findViewById(R.id.settings_button).setOnClickListener(v -> {
            // SettingActivity로 이동
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        // add_button 클릭 이벤트
        findViewById(R.id.add_button).setOnClickListener(v -> {
            // UploadActivity로 이동
            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
            startActivity(intent);
        });

        // folder_button 클릭 이벤트
        findViewById(R.id.folder_button).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FolderActivity.class);
            startActivity(intent);
        });
    }
}
