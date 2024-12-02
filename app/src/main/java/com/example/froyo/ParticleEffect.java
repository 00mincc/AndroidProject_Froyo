package com.example.froyo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ParticleEffect extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;

    private float[] particleX;
    private float[] particleY;
    private float[] speedX;
    private float[] speedY;
    private boolean[] isActive;  // 파티클 활성 상태 체크
    private int numParticles = 80;  // 파티클 개수
    private Handler handler;
    private Runnable updateRunnable;
    private Runnable spawnRunnable;  // 파티클 생성용 Runnable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particle_effect);

        imageView = findViewById(R.id.imageView);  // imageView 참조

        // Bitmap 및 Canvas 초기화
        bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setAntiAlias(true); // 부드러운 그리기 설정

        // 기본 화면에 이미지 설정
        imageView.setImageBitmap(bitmap);

        // Handler 및 Runnable 초기화
        handler = new Handler();

        // 파티클을 초기화하는 부분
        particleX = new float[numParticles];
        particleY = new float[numParticles];
        speedX = new float[numParticles];
        speedY = new float[numParticles];
        isActive = new boolean[numParticles]; // 파티클 활성 상태 초기화

        // 파티클을 1초마다 생성하도록 하는 Runnable
        spawnRunnable = new Runnable() {
            @Override
            public void run() {
                spawnParticles();  // 20개의 파티클을 랜덤 위치에 생성
                handler.postDelayed(this, 1000);  // 1초마다 실행
            }
        };

        // 파티클 애니메이션 업데이트를 위한 Runnable
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateParticles();  // 파티클 위치 업데이트
                imageView.setImageBitmap(bitmap);  // 화면 갱신
                handler.postDelayed(this, 20);  // 30ms마다 다시 실행
            }
        };

        // 파티클 생성 시작
        handler.post(spawnRunnable);

        // 파티클 애니메이션 시작
        handler.post(updateRunnable);

        // 10초 뒤에 MainActivity로 이동하는 코드 추가
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // MainActivity로 이동
                Intent mainIntent = new Intent(ParticleEffect.this, MainActivity.class);
                startActivity(mainIntent);
                finish();  // 현재 Activity 종료
            }
        }, 10000);  // 10초 뒤 실행
    }

    // 폭죽처럼 20개의 파티클을 화면의 랜덤 위치에서 생성
    private void spawnParticles() {
        // 랜덤한 생성 위치
        float startX = (float) (Math.random() * bitmap.getWidth());
        float startY = (float) (Math.random() * bitmap.getHeight());

        // 파티클 초기화
        for (int i = 0; i < numParticles; i++) {
            if (!isActive[i]) {
                // 파티클의 속도 설정 (랜덤한 각도와 속도)
                float angle = (float) (Math.random() * 360);  // 0~360도 사이의 각도
                speedX[i] = (float) (Math.cos(Math.toRadians(angle)) * 10f);  // X 방향 속도
                speedY[i] = (float) (Math.sin(Math.toRadians(angle)) * 10f);  // Y 방향 속도

                // 파티클 위치 설정 (랜덤한 위치에서 발생)
                particleX[i] = startX;
                particleY[i] = startY;
                isActive[i] = true;
            }
        }
    }

    // 파티클의 위치를 계속 업데이트
    private void updateParticles() {
        canvas.drawColor(Color.WHITE);  // 화면을 검은색으로 초기화

        // 각 파티클을 그리기
        for (int i = 0; i < numParticles; i++) {
            if (isActive[i]) {
                // 파티클 색상 설정 (랜덤 색상)
                paint.setColor(Color.rgb((int)(Math.random() * 256),
                        (int)(Math.random() * 256),
                        (int)(Math.random() * 256)));

                // 파티클 그리기
                canvas.drawCircle(particleX[i], particleY[i], 10, paint);  // 크기 10dp

                // 파티클의 이동
                particleX[i] += speedX[i];  // X 방향으로 이동
                particleY[i] += speedY[i];  // Y 방향으로 이동

                // 화면 밖으로 나가면 사라짐
                if (particleX[i] < 0 || particleX[i] > bitmap.getWidth() ||
                        particleY[i] < 0 || particleY[i] > bitmap.getHeight()) {
                    isActive[i] = false;  // 파티클 비활성화
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);  // Activity 종료 시 handler 정리
        handler.removeCallbacks(spawnRunnable);  // 파티클 생성 Runnable 정리
    }
}
