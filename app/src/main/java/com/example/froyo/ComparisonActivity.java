package com.example.froyo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ComparisonActivity extends AppCompatActivity {

    private static final String TAG = "ComparisonActivity";
    private List<Integer> answerCoordinates = new ArrayList<>(); // 정답 좌표 저장용 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);

        // 사용자 업로드 이미지
        ImageView userImageView = findViewById(R.id.userImageView);

        // 서버에서 받아온 이미지
        ImageView serverImageView = findViewById(R.id.serverImageView);

        // AppData에서 데이터 가져오기
        AppData appData = (AppData) getApplication();
        String srcBase64 = appData.getSrcBase64(); // 원본 이미지
        String dstBase64 = appData.getDstBase64(); // 변환 이미지
        String pstCoordinates = appData.getPstCoordinates(); // 정답 좌표

        // 정답 좌표 저장
        parseAnswerCoordinates(pstCoordinates);

        // src와 dst 이미지를 디코딩하여 ImageView에 표시
        displayImage(userImageView, srcBase64, "원본");
        displayImage(serverImageView, dstBase64, "변환");
    }

    /**
     * 정답 좌표 파싱 및 저장
     */
    private void parseAnswerCoordinates(String pstCoordinates) {
        if (pstCoordinates != null && !pstCoordinates.isEmpty()) {
            try {
                for (String coordinate : pstCoordinates.split(",")) {
                    answerCoordinates.add(Integer.parseInt(coordinate.trim()));
                }
                Log.i(TAG, "정답 좌표 저장 완료: " + answerCoordinates);
            } catch (NumberFormatException e) {
                Log.e(TAG, "정답 좌표 파싱 실패: " + pstCoordinates, e);
                Toast.makeText(this, "정답 좌표를 처리하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "정답 좌표 데이터가 없습니다.");
        }
    }

    /**
     * Base64 문자열을 디코딩하여 ImageView에 표시
     */
    private void displayImage(ImageView imageView, String base64String, String imageType) {
        if (base64String != null && !base64String.isEmpty()) {
            Bitmap bitmap = decodeBase64ToBitmap(base64String);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                Log.e(TAG, imageType + " 이미지를 디코딩할 수 없습니다.");
                Toast.makeText(this, imageType + " 이미지를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, imageType + " 이미지 데이터가 없습니다.");
            Toast.makeText(this, imageType + " 이미지 데이터가 제공되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Base64 문자열을 Bitmap으로 디코딩
     */
    private Bitmap decodeBase64ToBitmap(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Base64 디코딩 실패", e);
            return null;
        }
    }
}
