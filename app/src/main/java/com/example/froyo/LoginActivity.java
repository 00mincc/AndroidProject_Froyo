package com.example.froyo;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // activity_login.xml로 설정

        // XML에 정의된 버튼에 동작 추가
        Button startWithoutLoginButton = findViewById(R.id.first_button);
        startWithoutLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        });

        Button signupButton = findViewById(R.id.second_button);
        signupButton.setOnClickListener(v ->
                Toast.makeText(LoginActivity.this, "사용 불가능", Toast.LENGTH_SHORT).show()
        );

        Button existingAccountButton = findViewById(R.id.third_button);
        SpannableString content = new SpannableString("사용하는 계정이 있어요");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        existingAccountButton.setText(content);

        existingAccountButton.setOnClickListener(v ->
                Toast.makeText(LoginActivity.this, "사용 불가능", Toast.LENGTH_SHORT).show()
        );
    }
}
