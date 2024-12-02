package com.example.froyo;

import android.app.Application;

import java.util.List;

public class AppData extends Application {
    private String srcBase64;
    private String dstBase64;
    private List<List<Integer>> ptsCoordinates;

    public String getSrcBase64() {
        return srcBase64;
    }

    public void setSrcBase64(String srcBase64) {
        this.srcBase64 = srcBase64;
    }

    public String getDstBase64() {
        return dstBase64;
    }

    public void setDstBase64(String dstBase64) {
        this.dstBase64 = dstBase64;
    }

    public List<List<Integer>> getPtsCoordinates() {
        return ptsCoordinates;
    }

    public void setPtsCoordinates(List<List<Integer>> ptsCoordinates) {
        this.ptsCoordinates = ptsCoordinates;
    }
}