package com.example.asm2_android.View.Donor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.asm2_android.Controller.EventHistoryAdapter;
import com.example.asm2_android.Controller.EventHistoryFinishedAdapter;
import com.example.asm2_android.Model.EventHistoryClass;
import com.example.asm2_android.Model.RegisterEventClass;
import com.example.asm2_android.R;
import com.example.asm2_android.View.General.RegisterEventActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class DonorHistoryActivity extends AppCompatActivity {
    private RecyclerView eventHistoryRecyclerView;
    private  EventHistoryAdapter eventHistoryAdapter;
    private EventHistoryFinishedAdapter eventHistoryFinishedAdapter;
    private ArrayList<EventHistoryClass> upcomingEvents, completedEvents;
    private TabLayout tabLayout;

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

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("USERNAME", null);

        if (currentUser != null) {
            fetchEventDetails(currentUser);
        }


        eventHistoryRecyclerView = findViewById(R.id.eventHistoryRecyclerView);
        tabLayout = findViewById(R.id.tabLayout);
        eventHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        upcomingEvents = new ArrayList<>();
        eventHistoryAdapter = new EventHistoryAdapter(this, upcomingEvents);
        eventHistoryRecyclerView.setAdapter(eventHistoryAdapter);

        completedEvents = new ArrayList<>();
        eventHistoryFinishedAdapter = new EventHistoryFinishedAdapter(this, completedEvents);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    eventHistoryRecyclerView.setAdapter(eventHistoryAdapter);
                } else if (tab.getPosition() == 1) {
                    eventHistoryRecyclerView.setAdapter(eventHistoryFinishedAdapter);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_history);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_donation) {
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

    private void fetchEventDetails(String currentUsername) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("registers").whereEqualTo("username", currentUsername)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String address = document.getString("address");
                            String dob = document.getString("dateOfBirth");
                            String gender = document.getString("gender");
                            String bloodType = document.getString("bloodType");
                            String eventID = document.getString("eventID");
                            String bloodVolume = document.getString("bloodVolumeDonate");

                            db.collection("events")
                                    .document(eventID)
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful() && task2.getResult() != null) {
                                            DocumentSnapshot document2 = task2.getResult();
                                            String location = document2.getString("locationName");
                                            String eventDate = document2.getString("dateStart");
                                            Double latitude = document2.getDouble("latitude");
                                            Double longitude = document2.getDouble("longitude");
                                            Boolean open = document2.getBoolean("open");

                                            EventHistoryClass eventHistoryClass = new EventHistoryClass(name, address, dob,
                                                    gender, bloodType, bloodVolume, location, eventDate, latitude, longitude,eventID);

                                            if (open) {
                                                upcomingEvents.add(eventHistoryClass);
                                                eventHistoryAdapter.notifyDataSetChanged();
                                            } else {
                                                completedEvents.add(eventHistoryClass);
                                                eventHistoryFinishedAdapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            Log.w("fetchEventLocation", "Failed to retrieve event data or document does not exist.");
                                        }
                                    });
                        }
                    } else {
                        Log.w("fetchEventDetails", "No matching documents found or task failed.");
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