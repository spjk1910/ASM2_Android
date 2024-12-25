package com.example.asm2_android.View.SuperUser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.asm2_android.R;
import com.example.asm2_android.View.Donor.DonorHistoryActivity;
import com.example.asm2_android.View.Donor.DonorHomeActivity;
import com.example.asm2_android.View.Donor.DonorNotificationActivity;
import com.example.asm2_android.View.General.AccountManagementActivity;
import com.example.asm2_android.View.General.EditProfileActivity;
import com.example.asm2_android.View.General.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SuperUserProfileActivity extends AppCompatActivity {
    private LinearLayout edit_profile, log_out,account_management;
    private TextView name, username;
    private ShapeableImageView avatar;

    ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        boolean updateFlag = data.getBooleanExtra("updateFlag", false);
                        if (updateFlag) {
                            recreate();
                        }
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_user_profile);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        edit_profile = findViewById(R.id.edit_profile);
        log_out = findViewById(R.id.log_out);
        account_management = findViewById(R.id.account_management);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        avatar = findViewById(R.id.avatar);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_profile);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("USERNAME", null);
        if (currentUser != null) {
            fetchUserDetails(currentUser);
        }

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                editProfileLauncher.launch(intent);
            }
        });

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finishAffinity();
            }
        });

        account_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AccountManagementActivity.class));
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_map) {
                Intent intent = new Intent(getApplicationContext(), SuperUserHomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slider_in_right, R.anim.slider_out_left);
                finish();
            } else if (itemId == R.id.menu_profile) {
                return true;
            }
            return false;
        });
    }

    private void fetchUserDetails(String currentUsername) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("username", currentUsername)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String currentName = document.getString("name");
                        String currentAvatar = document.getString("profileImage");

                        name.setText(currentName);
                        username.setText("@"+currentUsername);

                        if (currentAvatar != null && !currentAvatar.isEmpty()) {
                            Glide.with(getApplicationContext())
                                    .load(currentAvatar)
                                    .circleCrop()
                                    .into(avatar);
                        } else {
                            avatar.setImageResource(R.drawable.default_avatar);
                        }
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