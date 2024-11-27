package com.example.froyo;

public class GenerateImageRequest {
    private String email;
    private String nickname;
    private String image_data;

    public GenerateImageRequest(String email, String nickname, String image_data) {
        this.email = email;
        this.nickname = nickname;
        this.image_data = image_data;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImageData() {
        return image_data;
    }

    public void setImageData(String image_data) {
        this.image_data = image_data;
    }
}
