package com.example.asm2_android.View.SiteManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm2_android.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class SiteManagerHomeActivity extends AppCompatActivity {
    private FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_manager_home);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        addButton = findViewById(R.id.add_button);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_manage);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_manage) {
                return true;
            } else if (itemId == R.id.menu_donation) {
                Intent intent = new Intent(getApplicationContext(), SiteManagerDonationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slider_in_right, R.anim.slider_out_left);
                finish();
                return true;
            } else if (itemId == R.id.menu_notification) {
                Intent intent = new Intent(getApplicationContext(), SiteManagerNotificationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slider_in_right, R.anim.slider_out_left);
                finish();
                return true;
            } else if (itemId == R.id.menu_profile) {
                Intent intent = new Intent(getApplicationContext(), SiteManagerProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slider_in_right, R.anim.slider_out_left);
                finish();
                return true;
            }
            return false;
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SiteManagerAddEventActivity.class);
                startActivity(intent);
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