package com.example.froyo;

public class ServerResponse {
    private String src; // 원본 이미지
    private String dst; // 변환 이미지
    private String pst; // 정답 좌표

    public String getSrc() {
        return src;
    }

    public String getDst() {
        return dst;
    }

    public String getPst() {
        return pst;
    }
}

