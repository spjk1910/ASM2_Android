package com.example.asm2_android.View.General;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm2_android.Controller.DonorListAdapter;
import com.example.asm2_android.Model.GenderEnum;
import com.example.asm2_android.Model.NotificationClass;
import com.example.asm2_android.Model.UserClass;
import com.example.asm2_android.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ListOfDonorActivity extends AppCompatActivity {
    private RecyclerView donorList;
    TextView backButton;
    private DonorListAdapter donorListAdapter;
    private ArrayList<UserClass> donorListItems;
    private TextView eventNameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_donor);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUser = sharedPreferences.getString("USERNAME", null);
        String eventID = getIntent().getStringExtra("EVENT_ID");
        String eventName = getIntent().getStringExtra("EVENT_NAME");

        backButton = findViewById(R.id.back_button);
        donorList = findViewById(R.id.donor_list);
        eventNameTV = findViewById(R.id.event_name);
        eventNameTV.setText(eventName);

        donorList.setLayoutManager(new LinearLayoutManager(this));
        donorListItems = new ArrayList<>();
        donorListAdapter = new DonorListAdapter(this, donorListItems);
        donorList.setAdapter(donorListAdapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (eventID != null) {
            loadDonorList(eventID);
        }
    }

    private void loadDonorList(String eventID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("EVENT_ID", "Event ID: " + eventID);
        db.collection("registers")
                .whereEqualTo("eventID", eventID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userID = document.getString("username");
                            Log.d("USER_ID", "User ID: " + userID);
                            if (userID != null) {
                                db.collection("users")
                                        .document(userID)
                                        .get()
                                        .addOnCompleteListener(userTask -> {
                                            if (userTask.isSuccessful() && userTask.getResult() != null) {
                                                DocumentSnapshot userDoc = userTask.getResult();
                                                if (userDoc.exists()) {
                                                    String name = userDoc.getString("name");
                                                    String email = userDoc.getString("email");
                                                    String username = userDoc.getString("username");
                                                    String phoneNumber = userDoc.getString("phoneNumber");
                                                    String address = userDoc.getString("address");
                                                    String bloodType = userDoc.getString("bloodType");
                                                    String profileImage = userDoc.getString("profileImage");
                                                    String birthday = userDoc.getString("birthday");
                                                    String gender = userDoc.getString("gender");

                                                    if (profileImage == null) {
                                                        profileImage = "https://firebasestorage.googleapis.com/v0/b/asm2-androiddev.firebasestorage.app/o/default_avatar.jpg?alt=media&token=c09c9c23-12dc-4ea9-abca-00503e663dfd";
                                                    }

                                                    UserClass user = new UserClass(name, email, username,null,
                                                            phoneNumber, address, bloodType, null, profileImage, birthday, GenderEnum.valueOf(gender));
                                                    if (user != null) {
                                                        donorListItems.add(user);
                                                        donorListAdapter.notifyDataSetChanged();

                                                        Log.d("User", "User details: " + user);
                                                    }
                                                } else {
                                                    Log.w("USER_ERROR", "No user document found for ID: " + userID);
                                                }
                                            } else {
                                                Log.w("USER_ERROR", "Error fetching user data: ", userTask.getException());
                                            }
                                        });
                            }
                        }
                    } else {
                        Log.w("REGISTER_ERROR", "Error fetching registered users: ", task.getException());
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
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }
}