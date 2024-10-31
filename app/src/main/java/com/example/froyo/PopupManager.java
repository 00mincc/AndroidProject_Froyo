package com.example.froyo;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class PopupManager {

    private final Activity activity;
    private OnUploadClickListener uploadClickListener;

    public PopupManager(Activity activity) {
        this.activity = activity;
    }

    // 팝업 UI를 띄우는 메서드
    public void showPopup() {
        RelativeLayout dimOverlay = activity.findViewById(R.id.dimOverlay);
        dimOverlay.setVisibility(View.VISIBLE);

        // 업로드 버튼 설정
        Button uploadButton = dimOverlay.findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(v -> {
            if (uploadClickListener != null) {
                uploadClickListener.onUploadClick(); // 클릭 시 리스너 호출
            }
            closePopup(); // 팝업 닫기
        });

        // dim 영역을 클릭하면 팝업을 닫습니다.
        dimOverlay.setOnClickListener(v -> closePopup());
    }

    // 팝업 창을 닫는 메서드
    public void closePopup() {
        RelativeLayout dimOverlay = activity.findViewById(R.id.dimOverlay);
        dimOverlay.setVisibility(View.GONE);
    }

    // 업로드 클릭 리스너 설정 메서드
    public void setOnUploadClickListener(OnUploadClickListener listener) {
        this.uploadClickListener = listener;
    }

    // 인터페이스 정의
    public interface OnUploadClickListener {
        void onUploadClick();
    }
}
