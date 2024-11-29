package com.example.froyo;

import java.util.List;

public class AnswerChecker {

    private final List<List<Integer>> answerCoordinates;

    /**
     * AnswerChecker 생성자
     * @param answerCoordinates 서버에서 받아온 정답 좌표 리스트
     */
    public AnswerChecker(List<List<Integer>> answerCoordinates) {
        this.answerCoordinates = answerCoordinates;
    }

    /**
     * 사용자가 선택한 좌표가 정답인지 확인
     * @param x 사용자가 클릭한 X 좌표
     * @param y 사용자가 클릭한 Y 좌표
     * @return 정답이면 true, 아니면 false
     */
    public boolean isCorrectAnswer(int x, int y) {
        for (List<Integer> coordinate : answerCoordinates) {
            if (coordinate.size() == 4) {
                int left = coordinate.get(0);  // 좌상단 X 좌표
                int top = coordinate.get(1);   // 좌상단 Y 좌표
                int right = coordinate.get(2); // 우하단 X 좌표
                int bottom = coordinate.get(3); // 우하단 Y 좌표

                // 클릭한 좌표가 정답 영역에 포함되는지 확인
                if (x >= left && x <= right && y >= top && y <= bottom) {
                    return true;
                }
            }
        }
        return false;
    }
}
