package com.example.froyo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class PopupManager {

    private final Activity activity;
    private View popupView;

    public PopupManager(Activity activity) {
        this.activity = activity;
    }

    // 팝업 UI를 띄우는 메서드
    public void showPopup() {
        if (popupView == null) {
            // 팝업 레이아웃 inflate
            popupView = LayoutInflater.from(activity).inflate(R.layout.settings_popup, null);

            // 팝업 추가
            activity.addContentView(popupView, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }

        popupView.setVisibility(View.VISIBLE);

        // 닫기 버튼 처리 (팝업 외부를 터치해도 닫히지 않도록 설정)
        popupView.setOnClickListener(null);
    }

    // 팝업 창을 닫는 메서드
    public void closePopup() {
        if (popupView != null) {
            popupView.setVisibility(View.GONE);
        }
    }
}
