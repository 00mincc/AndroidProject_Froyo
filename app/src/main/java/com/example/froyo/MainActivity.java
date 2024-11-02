package com.example.froyo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                        intent.putExtra("userImageUri", selectedImageUri.toString());
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "이미지를 선택하지 못했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openImagePicker();
                } else {
                    Toast.makeText(this, "이미지 업로드를 위해 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        goToSettingsForManageStoragePermission();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 문제 만들기 버튼
        Button createQuestionButton = findViewById(R.id.first_button);
        createQuestionButton.setOnClickListener(v -> checkPermissionAndOpenImagePicker());

        // 문제 목록 버튼
        Button questionListButton = findViewById(R.id.second_button);
        questionListButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuestionListActivity.class);
            startActivity(intent);
        });

        // 설정 버튼
        Button settingsButton = findViewById(R.id.third_button);
        settingsButton.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "사용 불가", Toast.LENGTH_SHORT).show());
    }

    private void checkPermissionAndOpenImagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                openImagePicker();
            } else {
                goToSettingsForManageStoragePermission();
            }
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void goToSettingsForManageStoragePermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
}
