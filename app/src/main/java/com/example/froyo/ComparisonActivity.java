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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ComparisonActivity extends AppCompatActivity {

    private static final String TAG = "ComparisonActivity";
    private int correctAnswers = 0;  // 맞춘 정답 개수
    private ParticleEffect particleEffect;  // ParticleEffect 객체 선언
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

            Log.i(TAG, "이미지 크기: " + serverBitmap.getWidth() + "px x " + serverBitmap.getHeight() + "px, " +
                    "변환된 크기: " + serverBitmap.getWidth() / getResources().getDisplayMetrics().density + "dp x " +
                    serverBitmap.getHeight() / getResources().getDisplayMetrics().density + "dp");

        } else {
            Log.e(TAG, "변환 이미지를 디코딩할 수 없습니다.");
        }

        // 터치 이벤트 추가
        serverImageView.setOnTouchListener((v, event) -> handleTouchEvent(event));

        // 정답 확인 버튼 이벤트
        checkAnswersButton.setOnClickListener(v -> drawAllAnswers());
    }

    /**
     * 이미지에 원을 그리기
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
     * 모든 정답에 원을 그리기
     */
    private void drawAllAnswers() {
        if (mutableBitmap == null) {
            Log.e(TAG, "Bitmap이 null입니다. 정답을 그릴 수 없습니다.");
            return;
        }

        // 기존에 그려진 모든 원을 지우고 새로 그리기
        mutableBitmap = serverBitmap.copy(Bitmap.Config.ARGB_8888, true); // 비트맵 복사

        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);  // 색상 변경 가능
        paint.setStyle(Paint.Style.STROKE); // 원의 내부를 비우기
        paint.setStrokeWidth(10);  // 테두리 두께
        paint.setAntiAlias(true);  // 가장자리 부드럽게

        // 정답 좌표 출력 확인
        Log.d(TAG, "정답 좌표 개수: " + ptsCoordinates.size());

        // 정답 좌표를 기준으로 원 그리기
        for (List<Integer> coordinates : ptsCoordinates) {
            if (coordinates.size() == 2) {
                float x1 = coordinates.get(0);
                float y1 = coordinates.get(1);
                int roundedX1 = Math.round(x1);
                int roundedY1 = Math.round(y1);

                // 좌표 로그
                Log.d(TAG, "좌표: (" + roundedX1 + ", " + roundedY1 + ")");
                // 원 그리기
                canvas.drawCircle(roundedX1, roundedY1, 50, paint);
            }
        }

        // 변경된 이미지를 ImageView에 다시 설정
        serverImageView.setImageBitmap(mutableBitmap);

        // Toast 메시지로 정답 표시 알림
        Toast.makeText(this, "정답이 표시되었습니다.", Toast.LENGTH_SHORT).show();
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

        // 이미지 비율을 고려한 계산 (실제 이미지 크기와 ImageView 크기의 비율)
        float scaleX = (float) bitmapWidth / (float) imageViewWidth;
        float scaleY = (float) bitmapHeight / (float) imageViewHeight;

        // 터치 좌표를 비율에 맞게 변환
        imageCoordinates[0] = touchX * scaleX;
        imageCoordinates[1] = touchY * scaleY;

        return imageCoordinates;
    }



    /**
     * 정답 체크 로직
     */
    private boolean isCorrectAnswer(int x, int y) {
        for (List<Integer> coordinates : ptsCoordinates) {
            int correctX = coordinates.get(0);
            int correctY = coordinates.get(1);

            // 좌표가 일정 범위 내에 있는지 확인
            if (Math.abs(x - correctX) < 50 && Math.abs(y - correctY) < 50) {
                return true;
            }
        }
        return false;
    }




    /**
     * 이미지를 Base64 문자열로부터 Bitmap으로 변환
     */
    private Bitmap decodeBase64ToBitmap(String base64Str) {
        try {
            byte[] decodedString = Base64.decode(base64Str, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            Log.e(TAG, "Base64 디코딩 오류", e);
            return null;
        }
    }

    /**
     * 이미지를 ImageView에 표시
     */
    private void displayImage(ImageView imageView, String base64Str, String label) {
        Bitmap bitmap = decodeBase64ToBitmap(base64Str);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            Log.e(TAG, label + " 이미지 로드 실패");
        }
    }
}