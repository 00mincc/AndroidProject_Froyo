package com.example.froyo;

import java.util.Stack;

public class UIManager {

    private String currentPage;
    private Stack<String> previousPages;

    public UIManager() {
        previousPages = new Stack<>();
    }

    // 현재 페이지를 저장
    public void setCurrentPage(String page) {
        if (currentPage != null) {
            previousPages.push(currentPage);
        }
        currentPage = page;
    }

    // 현재 페이지를 반환하는 메서드 (Getter)
    public String getCurrentPage() {
        return currentPage;
    }

    // 이전 페이지 스택에서 이전 페이지를 반환하는 메서드 (Getter)
    public String getPreviousPage() {
        return previousPages.isEmpty() ? null : previousPages.pop();
    }

    // 다음 UI 페이지로 이동하는 메서드
    public void navigateTo(String nextPage) {
        setCurrentPage(nextPage);
        // 여기에서 실제 UI 전환 로직을 추가합니다.
    }

    // 이전 페이지로 돌아가는 메서드
    public void navigateBack() {
        if (!previousPages.isEmpty()) {
            String previousPage = previousPages.pop();
            setCurrentPage(previousPage);
            // 여기에서 이전 페이지로 돌아가는 UI 전환 로직을 추가합니다.
        }
    }
}