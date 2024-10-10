package com.example.froyo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private UIManager uiManager;
    private PopupManager popupManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UIManager와 PopupManager 초기화
        uiManager = new UIManager();
        popupManager = new PopupManager(this);

        // 첫 번째 버튼: 페이지 이동
        Button nextPageaButton = findViewById(R.id.NextPageButton);
        nextPageaButton.setOnClickListener(v -> {
            uiManager.setCurrentPage("MainActivity");
            navigateToSecondPage();
        });

        // 팝업 띄우기 버튼
        Button showPopupButton = findViewById(R.id.showPopupButton);
        showPopupButton.setOnClickListener(v -> {
            popupManager.showPopup();
        });
    }

    // 두 번째 페이지로 이동하는 메서드
    private void navigateToSecondPage() {
        uiManager.setCurrentPage("SecondActivity");
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    }

    // 팝업 닫기 버튼 클릭 이벤트 처리
    public void closePopupButtonClick(android.view.View view) {
        popupManager.closePopup(); // 팝업만 닫습니다.
    }
}
