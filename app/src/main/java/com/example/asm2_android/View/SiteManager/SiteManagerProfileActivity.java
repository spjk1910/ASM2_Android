package com.example.asm2_android.View.SiteManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.asm2_android.Controller.NotificationAdapter;
import com.example.asm2_android.Model.NotificationClass;
import com.example.asm2_android.R;
import com.example.asm2_android.View.General.AccountManagementActivity;
import com.example.asm2_android.View.General.EditProfileActivity;
import com.example.asm2_android.View.General.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SiteManagerProfileActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_site_manager_profile);
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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_manage) {
                Intent intent = new Intent(getApplicationContext(), SiteManagerHomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slider_in_right, R.anim.slider_out_left);
                finish();
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

    public static class SiteManagerNotificationActivity extends AppCompatActivity {
        private RecyclerView notification;
        private NotificationAdapter notificationAdapter;
        private ArrayList<NotificationClass> notificationList;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_site_manager_notification);
            Objects.requireNonNull(getSupportActionBar()).hide();
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );

            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String currentUser = sharedPreferences.getString("USERNAME", null);

            notification = findViewById(R.id.notification);
            notification.setLayoutManager(new LinearLayoutManager(this));
            notificationList = new ArrayList<>();
            notificationAdapter = new NotificationAdapter(this, notificationList);
            notification.setAdapter(notificationAdapter);

            loadNotification(currentUser);

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.menu_notification);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_manage) {
                    Intent intent = new Intent(getApplicationContext(), SiteManagerHomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slider_in_right, R.anim.slider_out_left);
                    finish();
                    return true;
                } else if (itemId == R.id.menu_donation) {
                    Intent intent = new Intent(getApplicationContext(), SiteManagerDonationActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slider_in_right, R.anim.slider_out_left);
                    finish();
                    return true;
                } else if (itemId == R.id.menu_notification) {
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
        }

        private void loadNotification(String username) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("notifications")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String content = document.getString("content");
                                Timestamp time = document.getTimestamp("time");
                                String eventID = document.getString("eventID");

                                if (time != null) {
                                    Date date = time.toDate();
                                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
                                    String formattedDate = sdf.format(date);

                                    Log.d("TIME_CHECK", "Time: " + formattedDate);

                                    NotificationClass notificationClass = new NotificationClass(formattedDate, content,eventID);

                                    notificationList.add(notificationClass);
                                    notificationAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            Log.w("NOTIFICATION_ERROR", "Error getting notifications: ", task.getException());
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
}