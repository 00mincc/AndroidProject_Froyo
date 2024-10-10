package com.example.froyo;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;

public class PopupManager {

    private Activity activity;

    public PopupManager(Activity activity) {
        this.activity = activity;
    }

    // 팝업 UI를 띄우는 메서드
    public void showPopup() {
        RelativeLayout dimOverlay = activity.findViewById(R.id.dimOverlay);
        dimOverlay.setVisibility(View.VISIBLE);

        // dim 영역을 클릭하면 팝업을 닫습니다.
        dimOverlay.setOnClickListener(v -> {
            closePopup();
        });
    }

    // 팝업 창을 닫는 메서드
    public void closePopup() {
        RelativeLayout dimOverlay = activity.findViewById(R.id.dimOverlay);
        dimOverlay.setVisibility(View.GONE);
    }
}
