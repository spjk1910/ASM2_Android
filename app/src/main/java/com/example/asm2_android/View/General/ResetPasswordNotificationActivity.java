package com.example.asm2_android.View.General;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asm2_android.R;

import java.util.Objects;

public class ResetPasswordNotificationActivity extends AppCompatActivity {
    private TextView backButton, password;
    private EditText code;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_notification);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        String secretCode = getIntent().getStringExtra("CODE");
        String pass = getIntent().getStringExtra("PASSWORD");

        backButton = findViewById(R.id.back_button);
        code = findViewById(R.id.code);
        confirmButton = findViewById(R.id.confirm_button);
        password = findViewById(R.id.password);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentCode = code.getText().toString();
                if (currentCode.isEmpty() || !currentCode.equals(secretCode)) {
                    code.setError(currentCode.isEmpty() ? "Code is required" : "Invalid code");
                    code.requestFocus();
                    return;
                } else {
                    String currentPassword = "Your Password is: " + pass;
                    password.setText(currentPassword);
                    password.setVisibility(View.VISIBLE);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }
}