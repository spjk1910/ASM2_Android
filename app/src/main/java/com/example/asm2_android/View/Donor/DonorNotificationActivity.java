package com.example.asm2_android.View.Donor;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm2_android.Controller.NotificationAdapter;
import com.example.asm2_android.Model.EventHistoryClass;
import com.example.asm2_android.Model.NotificationClass;
import com.example.asm2_android.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DonorNotificationActivity extends AppCompatActivity {
    private RecyclerView notification;
    private NotificationAdapter notificationAdapter;
    private ArrayList<NotificationClass> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_notification);
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
            if (itemId == R.id.menu_donation) {
                startActivity(new Intent(getApplicationContext(), DonorHomeActivity.class));
                overridePendingTransition(R.anim.slider_in_right, R.anim.slider_out_left);
                finish();
                return true;
            } else if (itemId == R.id.menu_history) {
                startActivity(new Intent(getApplicationContext(), DonorHistoryActivity.class));
                overridePendingTransition(R.anim.slider_in_right, R.anim.slider_out_left);
                finish();
                return true;
            } else if (itemId == R.id.menu_notification) {
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