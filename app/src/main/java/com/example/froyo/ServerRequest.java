
package com.example.froyo;

public class ServerRequest {
    private String email;
    private String nickname;
    private String image_data;

    public ServerRequest(String email, String nickname, String image_data) {
        this.email = email;
        this.nickname = nickname;
        this.image_data = image_data;
    }
}
