package com.example.asm2_android.View.Donor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asm2_android.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class DonorHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_history);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_history);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_map) {
                startActivity(new Intent(getApplicationContext(), DonorHomeActivity.class));
                overridePendingTransition(R.anim.slider_in_right, R.anim.slider_out_left);
                finish();
                return true;
            } else if (itemId == R.id.menu_history) {
                return true;
            } else if (itemId == R.id.menu_notification) {
                startActivity(new Intent(getApplicationContext(), DonorNotificationActivity.class));
                overridePendingTransition(R.anim.slider_in_right, R.anim.slider_out_left);
                finish();
                return true;
            } else if (itemId == R.id.menu_profile) {
                startActivity(new Intent(getApplicationContext(), DonorProfileActivity.class));
                overridePendingTransition(R.anim.slider_in_right, R.anim.slider_out_left);
                finish();
                return true;
            }
            return false;
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