package com.example.asm2_android.View.General;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2_android.Model.UserRoleEnum;
import com.example.asm2_android.R;
import com.example.asm2_android.View.Donor.DonorHomeActivity;
import com.example.asm2_android.View.SiteManager.SiteManagerDonationActivity;

import java.util.Objects;

public class FailedRegisterActivity extends AppCompatActivity {
    private TextView reason;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed_register);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        reason = findViewById(R.id.reason);
        backButton = findViewById(R.id.ic_back);

        Intent intent = getIntent();
        boolean ageFlag = intent.getBooleanExtra("AGE_FLAG", false);
        boolean weightFlag = intent.getBooleanExtra("WEIGHT_FLAG", false);
        String reasonStr = "";
        if (!ageFlag) {
            reasonStr += "You must be older than 18 and under 65 years of age\n\n";
        }
        if(!weightFlag){
            reasonStr += "You need to weigh at least 42 kg if female or 45 kg if male";
        }

        reason.setText(reasonStr);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userRole = getUserRole();

                if (UserRoleEnum.DONORS.name().equals(userRole)) {
                    Intent  intent = new Intent(FailedRegisterActivity.this, DonorHomeActivity.class);
                    startActivity(intent);
                } else if (UserRoleEnum.SITE_MANAGERS.name().equals(userRole)) {
                    Intent intent = new Intent(FailedRegisterActivity.this, SiteManagerDonationActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });
    }
    private String getUserRole() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return prefs.getString("userRole", "");
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