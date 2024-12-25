package com.example.asm2_android.View.SiteManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm2_android.Controller.AddEventListAdapter;
import com.example.asm2_android.Controller.AddEventListFinishedAdapter;
import com.example.asm2_android.Model.BloodTypeDetails;
import com.example.asm2_android.Model.EventDetailClass;
import com.example.asm2_android.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SiteManagerHomeActivity extends AppCompatActivity {
    public interface BloodTypeCallback {
        void onCallback(BloodTypeDetails bloodTypeDetails);
    }

    private RecyclerView eventManagementRecyclerView;
    private AddEventListAdapter addEventListAdapter;
    private AddEventListFinishedAdapter addEventListFinishedAdapter;
    private ArrayList<EventDetailClass> upcomingEvents, completedEvents;
    private TabLayout tabLayout;
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

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("USERNAME", null);

        if (currentUser != null) {
            fetchEventDetails(currentUser);
        }

        eventManagementRecyclerView = findViewById(R.id.eventManagementRecyclerView);
        tabLayout = findViewById(R.id.tabLayout);
        eventManagementRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        upcomingEvents = new ArrayList<>();
        addEventListAdapter = new AddEventListAdapter(this, upcomingEvents);
        eventManagementRecyclerView.setAdapter(addEventListAdapter);

        completedEvents = new ArrayList<>();
        addEventListFinishedAdapter = new AddEventListFinishedAdapter(this, completedEvents);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    eventManagementRecyclerView.setAdapter(addEventListAdapter);
                } else if (tab.getPosition() == 1) {
                    eventManagementRecyclerView.setAdapter(addEventListFinishedAdapter);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

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
                Intent intent = new Intent(getApplicationContext(), SiteManagerProfileActivity.SiteManagerNotificationActivity.class);
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

    private void fetchEventDetails(String currentUsername) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").whereEqualTo("siteManagerUsername", currentUsername)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String eventName = document.getString("eventName");
                            String siteManagerName = document.getString("siteManagerName");
                            String locationName = document.getString("locationName");
                            String eventDate = document.getString("dateStart");
                            Double latitude = document.getDouble("latitude");
                            Double longitude = document.getDouble("longitude");
                            Boolean open = document.getBoolean("open");
                            String contact = document.getString("contact");
                            String eventMission = document.getString("eventMission");
                            String eventID = document.getId();

                            String locationAddress = findLocationAddress(latitude, longitude);

                            calculateBloodAmountByBloodType(db, eventID, bloodTypeDetails -> {
                                EventDetailClass eventDetailClass = new EventDetailClass(
                                        bloodTypeDetails.getBloodTypeAPlus(), bloodTypeDetails.getBloodTypeBPlus(),
                                        bloodTypeDetails.getBloodTypeABPlus(), bloodTypeDetails.getBloodTypeOPlus(),
                                        bloodTypeDetails.getBloodTypeAMinus(), bloodTypeDetails.getBloodTypeBMinus(),
                                        bloodTypeDetails.getBloodTypeABMinus(), bloodTypeDetails.getBloodTypeOMinus(),
                                        bloodTypeDetails.getTotalBloodAmount(), eventName, siteManagerName,
                                        locationName, latitude, longitude, locationAddress, eventDate,
                                        eventID, contact, eventMission
                                );

                                runOnUiThread(() -> {
                                    if (open) {
                                        upcomingEvents.add(eventDetailClass);
                                        addEventListAdapter.notifyDataSetChanged();
                                    } else {
                                        completedEvents.add(eventDetailClass);
                                        addEventListFinishedAdapter.notifyDataSetChanged();
                                    }
                                });
                            });
                        }
                    } else {
                        Log.w("fetchEventDetails", "No matching documents found or task failed.");
                    }
                });
    }

    private void calculateBloodAmountByBloodType(FirebaseFirestore db, String eventID, BloodTypeCallback callback) {
        final int[] bloodTypeAPlus = {0};
        final int[] bloodTypeBPlus = {0};
        final int[] bloodTypeABPlus = {0};
        final int[] bloodTypeOPlus = {0};
        final int[] bloodTypeAMinus = {0};
        final int[] bloodTypeBMinus = {0};
        final int[] bloodTypeABMinus = {0};
        final int[] bloodTypeOMinus = {0};
        final int[] totalBloodAmount = {0};

        db.collection("registers").whereEqualTo("eventID", eventID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String bloodType = document.getString("bloodType");
                            String bloodAmountStr = document.getString("bloodVolumeDonate");

                            int bloodAmount = extractBloodAmount(bloodAmountStr);
                            totalBloodAmount[0] += bloodAmount;

                            switch (bloodType) {
                                case "A+":
                                    bloodTypeAPlus[0] += bloodAmount;
                                    break;
                                case "B+":
                                    bloodTypeBPlus[0] += bloodAmount;
                                    break;
                                case "AB+":
                                    bloodTypeABPlus[0] += bloodAmount;
                                    break;
                                case "O+":
                                    bloodTypeOPlus[0] += bloodAmount;
                                    break;
                                case "A-":
                                    bloodTypeAMinus[0] += bloodAmount;
                                    break;
                                case "B-":
                                    bloodTypeBMinus[0] += bloodAmount;
                                    break;
                                case "AB-":
                                    bloodTypeABMinus[0] += bloodAmount;
                                    break;
                                case "O-":
                                    bloodTypeOMinus[0] += bloodAmount;
                                    break;
                                default:
                                    Log.w("calculateBloodAmountByBloodType", "Unknown blood type: " + bloodType);
                            }
                        }
                    } else {
                        Log.w("calculateBloodAmountByBloodType", "No matching documents found or task failed.");
                    }

                    callback.onCallback(new BloodTypeDetails(
                            bloodTypeAPlus[0], bloodTypeBPlus[0], bloodTypeABPlus[0], bloodTypeOPlus[0],
                            bloodTypeAMinus[0], bloodTypeBMinus[0], bloodTypeABMinus[0], bloodTypeOMinus[0],
                            totalBloodAmount[0]
                    ));
                });
    }

    private int extractBloodAmount(String bloodAmountStr) {
        try {
            String numericValue = bloodAmountStr.split(" ")[0];
            return Integer.parseInt(numericValue);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Log.w("extractBloodAmount", "Invalid blood amount format: " + bloodAmountStr);
            return 0;
        }
    }

    public static String findLocationAddress(double latitude, double longitude) {
        String apiKey = "AIzaSyB_V9ShUaUaIP_z-7Vmd0-huVh-BvNq-6U";
        String urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                + latitude + "," + longitude + "&key=" + apiKey;

        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() {
                String address = null;
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    String response = stringBuilder.toString();

                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getJSONArray("results").length() > 0) {
                        address = jsonResponse.getJSONArray("results").getJSONObject(0)
                                .getString("formatted_address");
                    }
                } catch (Exception e) {
                    Log.e("LocationHelper", "Error fetching address", e);
                }
                return address;
            }
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(callable);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        } finally {
            executor.shutdown();
        }
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