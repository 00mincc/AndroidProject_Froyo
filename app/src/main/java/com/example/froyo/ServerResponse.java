package com.example.froyo;

import com.google.gson.annotations.SerializedName;
import java.util.List;



public class ServerResponse {

    @SerializedName("src")
    private String src;

    @SerializedName("dst")
    private String dst;

    @SerializedName("pts")
    private List<List<Integer>> pts;  // Integer -> Float로 수정

    public String getSrc() {
        return src;
    }

    public String getDst() {
        return dst;
    }

    public List<List<Integer>> getPts() {
        return pts;
    }

    @Override
    public String toString() {
        return "ServerResponse{" +
                "src='" + src + '\'' +
                ", dst='" + dst + '\'' +
                ", pts=" + pts +
                '}';
    }
}
