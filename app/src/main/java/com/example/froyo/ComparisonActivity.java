package com.example.froyo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ComparisonActivity extends AppCompatActivity {

    private static final String TAG = "ComparisonActivity";
    private List<List<Integer>> ptsCoordinates; // 서버에서 받아온 정답 좌표 리스트
    private Bitmap serverBitmap; // 변환 이미지
    private ImageView serverImageView; // 변환 이미지뷰
    private Bitmap mutableBitmap; // 수정 가능한 비트맵

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);

        ImageView userImageView = findViewById(R.id.userImageView);
        serverImageView = findViewById(R.id.serverImageView);
        Button checkAnswersButton = findViewById(R.id.checkAnswersButton);

        // AppData에서 데이터 가져오기
        AppData appData = (AppData) getApplication();
        String srcBase64 = appData.getSrcBase64();
        String dstBase64 = appData.getDstBase64();
        ptsCoordinates = appData.getPtsCoordinates(); // 서버에서 전달받은 좌표 사용

        Log.i(TAG, "pts 데이터 확인: " + ptsCoordinates);

        // 이미지 표시
        displayImage(userImageView, srcBase64, "원본");
        serverBitmap = decodeBase64ToBitmap(dstBase64);
        if (serverBitmap != null) {
            // 서버 이미지를 MutableBitmap으로 설정
            mutableBitmap = serverBitmap.copy(Bitmap.Config.ARGB_8888, true);
            serverImageView.setImageBitmap(mutableBitmap);
        } else {
            Log.e(TAG, "변환 이미지를 디코딩할 수 없습니다.");
        }

        // 터치 이벤트 추가
        serverImageView.setOnTouchListener((v, event) -> handleTouchEvent(event));

        // 정답 확인 버튼 이벤트
        checkAnswersButton.setOnClickListener(v -> drawAllAnswers());
    }

    /**
     * 터치 이벤트 처리
     */
    private boolean handleTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // 터치한 좌표
            float touchX = event.getX();
            float touchY = event.getY();

            Log.d(TAG, "터치 좌표: (" + touchX + ", " + touchY + ")");

            // ImageView와 Bitmap 간 비율 계산
            float[] imageCoordinates = mapTouchToImage(touchX, touchY);

            if (imageCoordinates == null) {
                Toast.makeText(this, "좌표 변환에 실패했습니다.", Toast.LENGTH_SHORT).show();
                return false;
            }

            int imageX = (int) imageCoordinates[0];
            int imageY = (int) imageCoordinates[1];

            Log.d(TAG, "이미지 좌표: (" + imageX + ", " + imageY + ")");

            // 정답 체크
            if (isCorrectAnswer(imageX, imageY)) {
                Toast.makeText(this, "정답입니다!", Toast.LENGTH_SHORT).show();
                drawCircleOnAnswer(imageX, imageY);
            } else {
                Toast.makeText(this, "오답입니다. 다시 시도해 보세요!", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    /**
     * ImageView 터치 좌표를 실제 이미지 좌표로 변환
     */
    private float[] mapTouchToImage(float touchX, float touchY) {
        float[] imageCoordinates = new float[2];

        // ImageView 크기
        int imageViewWidth = serverImageView.getWidth();
        int imageViewHeight = serverImageView.getHeight();

        // Bitmap 크기
        int bitmapWidth = mutableBitmap.getWidth();
        int bitmapHeight = mutableBitmap.getHeight();

        // ImageView와 Bitmap의 비율 계산
        float scaleX = (float) bitmapWidth / imageViewWidth;
        float scaleY = (float) bitmapHeight / imageViewHeight;

        // 실제 이미지 좌표 계산
        imageCoordinates[0] = touchX * scaleX;
        imageCoordinates[1] = touchY * scaleY;

        return imageCoordinates;
    }

    /**
     * 정답 체크 로직
     */
    private boolean isCorrectAnswer(int x, int y) {
        if (ptsCoordinates == null || ptsCoordinates.isEmpty()) {
            Log.w(TAG, "정답 좌표가 없습니다.");
            return false;
        }

        int tolerance = 20; // 허용 범위 (픽셀 단위로 설정)

        for (List<Integer> coordinates : ptsCoordinates) {
            if (coordinates.size() == 4) {
                int x1 = coordinates.get(0);
                int y1 = coordinates.get(1);
                int x2 = coordinates.get(2);
                int y2 = coordinates.get(3);

                // 터치 좌표가 허용 범위를 포함한 정답 영역 내에 있는지 확인
                if (x >= x1 - tolerance && x <= x2 + tolerance &&
                        y >= y1 - tolerance && y <= y2 + tolerance) {
                    Log.i(TAG, "정답 좌표 확인: (" + x1 + ", " + y1 + ", " + x2 + ", " + y2 + ") + 허용 범위: " + tolerance);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 정답 영역에 빨간색 원을 그리기
     */
    private void drawCircleOnAnswer(int x, int y) {
        if (mutableBitmap == null) {
            Log.e(TAG, "Bitmap이 null입니다. 원을 그릴 수 없습니다.");
            return;
        }

        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE); // 내부 비우기
        paint.setStrokeWidth(10); // 테두리 두께
        paint.setAntiAlias(true);

        // 원 크기 설정
        int radius = 50;
        canvas.drawCircle(x, y, radius, paint);

        // 변경된 이미지를 ImageView에 다시 설정
        serverImageView.setImageBitmap(mutableBitmap);
    }

    /**
     * 모든 정답을 표시
     */
    private void drawAllAnswers() {
        if (mutableBitmap == null) {
            Log.e(TAG, "Bitmap이 null입니다. 정답을 그릴 수 없습니다.");
            return;
        }

        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE); // 내부 비우기
        paint.setStrokeWidth(10); // 테두리 두께
        paint.setAntiAlias(true);

        for (List<Integer> coordinates : ptsCoordinates) {
            if (coordinates.size() == 4) {
                int x1 = coordinates.get(0);
                int y1 = coordinates.get(1);
                int x2 = coordinates.get(2);
                int y2 = coordinates.get(3);

                int centerX = (x1 + x2) / 2;
                int centerY = (y1 + y2) / 2;
                int radius = Math.min((x2 - x1) / 2, (y2 - y1) / 2);

                canvas.drawCircle(centerX, centerY, radius, paint);
            }
        }

        serverImageView.setImageBitmap(mutableBitmap);
        Toast.makeText(this, "정답이 표시되었습니다.", Toast.LENGTH_SHORT).show();
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
