package com.example.froyo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FolderActivity extends AppCompatActivity {

    private static final String TAG = "FolderActivity";
    private LinearLayout imageListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        // 폴더 관리 화면 초기화
        Toast.makeText(this, "폴더 관리 화면입니다.", Toast.LENGTH_SHORT).show();

        imageListContainer = findViewById(R.id.image_list_container);

        // 저장된 이미지 표시
        displaySavedSessions();

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

    private void displaySavedSessions() {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && storageDir.exists()) {
            File[] sessionFolders = storageDir.listFiles();
            if (sessionFolders != null) {
                for (File sessionFolder : sessionFolders) {
                    if (sessionFolder.isDirectory() && sessionFolder.getName().startsWith("session_")) {
                        addSessionToList(sessionFolder);
                    }
                }
            }
        } else {
            Log.e(TAG, "저장 디렉터리를 찾을 수 없습니다.");
        }
    }

    private void addSessionToList(File sessionFolder) {
        File srcImageFile = new File(sessionFolder, "src.png");
        if (!srcImageFile.exists()) return;

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(srcImageFile));

            // 세션 목록 항목 레이아웃을 동적으로 생성
            View itemView = LayoutInflater.from(this).inflate(R.layout.list_item_image, imageListContainer, false);

            // 이미지 설정
            ImageView imageView = itemView.findViewById(R.id.image_view);
            imageView.setImageBitmap(bitmap);

            // 세션 이름 설정
            TextView imageNameView = itemView.findViewById(R.id.image_name);
            imageNameView.setText(sessionFolder.getName());

            // 이미지 생성 날짜 설정
            TextView imageDateView = itemView.findViewById(R.id.image_date);
            String formattedDate = new SimpleDateFormat("yyyy / MM / dd").format(new Date(sessionFolder.lastModified()));
            imageDateView.setText(formattedDate);

            // 항목을 길게 눌렀을 때 옵션 제공
            itemView.setOnLongClickListener(v -> {
                showOptionsDialog(sessionFolder, itemView);
                return true;
            });

            // 항목을 이미지 리스트 컨테이너에 추가
            imageListContainer.addView(itemView);

        } catch (FileNotFoundException e) {
            Log.e(TAG, "이미지 파일을 찾을 수 없습니다: " + srcImageFile.getName(), e);
        }
    }

    private void showOptionsDialog(File sessionFolder, View itemView) {
        new AlertDialog.Builder(this)
                .setTitle("옵션 선택")
                .setItems(new String[]{"게임 실행", "삭제"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            startGame(sessionFolder);
                            break;
                        case 1:
                            deleteSession(sessionFolder, itemView);
                            break;
                    }
                })
                .create()
                .show();
    }

    private void startGame(File sessionFolder) {
        // ComparisonActivity로 이동하고 해당 세션의 데이터를 로드
        Intent intent = new Intent(FolderActivity.this, ComparisonActivity.class);
        intent.putExtra("sessionFolderPath", sessionFolder.getAbsolutePath());
        startActivity(intent);
    }


    private void deleteSession(File sessionFolder, View itemView) {
        // 폴더와 하위 파일 삭제
        if (deleteRecursive(sessionFolder)) {
            imageListContainer.removeView(itemView);
            Toast.makeText(this, "세션이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "세션 삭제 성공: " + sessionFolder.getName());
        } else {
            Toast.makeText(this, "세션 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "세션 삭제 실패: " + sessionFolder.getName());
        }
    }

    private boolean deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        return fileOrDirectory.delete();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right); // 오른쪽으로 이동
    }
}